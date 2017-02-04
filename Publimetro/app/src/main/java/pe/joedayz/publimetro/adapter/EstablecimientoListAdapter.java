package pe.joedayz.publimetro.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import pe.joedayz.publimetro.AppController;
import pe.joedayz.publimetro.R;
import pe.joedayz.publimetro.model.Establecimiento;

/**
 * Created by josediaz on 2/20/16.
 */
public class EstablecimientoListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Establecimiento> establecimientoList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public EstablecimientoListAdapter(Activity activity, List<Establecimiento> establecimientoList) {
        this.activity = activity;
        this.establecimientoList = establecimientoList;
    }


    @Override
    public int getCount() {
        return establecimientoList.size();
    }

    @Override
    public Object getItem(int position) {
        return establecimientoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_publiguia, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);

        TextView titulo = (TextView) convertView.findViewById(R.id.titulo);

        TextView descripcion = (TextView) convertView.findViewById(R.id.descripcion);

        Establecimiento establecimiento = establecimientoList.get(position);

        thumbNail.setImageUrl(establecimiento.getFileNameLogo(), imageLoader);
        titulo.setText(establecimiento.getTitulo());
        descripcion.setText(establecimiento.getResumen());

        return convertView;
    }
}
