package mx.ferreyra.dogapp;

import mx.ferreyra.dogapp.pojos.FotosMascotaByUsuarioMesAnoResponse;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class DialogPhotoOptions {

    private FotosMascotaByUsuarioMesAnoResponse fotoMascota;
    private Dialog dialog;
    private Context context;

    private Button details;
    private Button edit;
    private Button share;
    private Button cancel;

    public DialogPhotoOptions (FotosMascotaByUsuarioMesAnoResponse fotoMascota, Context context ){
        this.fotoMascota = fotoMascota;
        this.context = context;
        this.dialog = new Dialog(context);    
    }

    public void build (){
        this.dialog.setContentView(R.layout.dialog_options_photo);
        this.dialog.setTitle("Opciones");
        this.dialog.setCancelable(true);


        Button details = (Button) this.dialog.findViewById(R.id.bt_details);
        Button edit = (Button) this.dialog.findViewById(R.id.bt_edit);
        Button share = (Button) this.dialog.findViewById(R.id.bt_share);
        Button cancel = (Button) this.dialog.findViewById(R.id.bt_cancel);


        details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent (context, ShowDogPhoto.class);
                intent.putExtra(ShowDogPhoto.PHOTO_ID, fotoMascota.getPhotoId().intValue());
                context.startActivity(intent);

            }
        });


        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();


                Intent intent = new Intent(context, AddDogPhoto.class);
                intent.putExtra("FOTO_MASCOTA", fotoMascota);

                context.startActivity(intent);


            }
        });


        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                Intent i = new Intent(Intent.ACTION_SEND);//new Intent(Intent.ACTION_VIEW);  
                //i.setData(Uri.parse(url));
                i.putExtra(Intent.EXTRA_TEXT, "Tomando un recuerdo de mi perro con DogChow ");
                i.setType("text/plain");
                context.startActivity(i);
            }
        });


        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


    }


    public void buildShow (){
        this.build();
        this.dialog.show();
    }


}
