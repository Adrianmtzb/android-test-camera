package mx.adrianmb.testphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST = 99;
    Uri uri;

    //Son necesarios los permisos de acceso a camara y lectura/escritura de external storage en el manifest
    //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    //<uses-feature android:name="android.hardware.camera"android:required="true" />

    //Para versiones superiores a API 23 es necesario solicitar permisos en tiempo de ejecucion y hacer uso de la clase FileProvider
    private ImageView imageView_fotoEvento;
    private Button btnTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView_fotoEvento = findViewById(R.id.imageView);
        btnTakePhoto = findViewById(R.id.button);

        btnTakePhoto.setOnClickListener(v -> TomarFoto());
//        checkPermission();
    }

    private void TomarFoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            //Se maneja la excepcion de creacion de archivo temporal con un bloque try/catch
            try {
                uri = Uri.fromFile(getOutputMediaFile());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private File getOutputMediaFile() throws Exception {

        Log.w("STORAGE STATE", Environment.getExternalStorageState());

        Log.w("PICTURES DIR", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).canWrite() ? "Can write" : "Can't write");
        Log.w("PICTURES DIR", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).canRead() ? "Can read" : "Can't read");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InnerDrive_Temp");

        if (!mediaStorageDir.canWrite() || !mediaStorageDir.canRead())
            throw new Exception("No tienes acceso de lectura/escritura en el directorio");

        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdirs())
                throw new Exception("No se pudo crear el archivo temporal para guardar la foto");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "_img.jpg");
        //mCurrentPhotoPath = mediaFile.toString(); No es necesario
        Log.w("MEDIA FILE", mediaFile.toString());
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (resultCode == RESULT_OK) {
                        Bitmap mImageBitmap = BitmapFactory.decodeFile(uri.getPath());
                        //streamImageEvento no estaba definido
                        ByteArrayOutputStream streamImageEvento = new ByteArrayOutputStream();
                        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, streamImageEvento);
                        mImageBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(streamImageEvento.toByteArray()));

                        imageView_fotoEvento.setImageBitmap(mImageBitmap);
                    }
                    break;
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

//    private void checkPermission() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST);
//
//        } else {
//
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    //
//                } else {
//                    checkPermission();
//                }
//                return;
//            }
//        }
//    }
}
