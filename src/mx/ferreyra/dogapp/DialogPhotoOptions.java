package mx.ferreyra.dogapp;

import mx.ferreyra.dogapp.pojos.FotosMascotaByUsuarioMesAnoResponse;
import android.app.Dialog;
import android.content.Context;
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
