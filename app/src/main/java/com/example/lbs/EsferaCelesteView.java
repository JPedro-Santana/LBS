package com.example.lbs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GnssStatus;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EsferaCelesteView extends View {
    private Location newLocation;
    private GnssStatus newStatus;
    private Paint paint;
    private int r;
    private int height,width;

    /* public ImageView(Context context) {
        super(context);
        ImageView = new ArrayList<>();

        // Inicializar os dados dos satélites (exemplo)
        satellites.add(new Satellite(1, "GPS", true, 45, 30));
        satellites.add(new Satellite(2, "Galileo", false, 60, 45));
        satellites.add(new Satellite(3, "Glonass", true, 90, 60));
    }*/

    public EsferaCelesteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint=new Paint();
    }

    public EsferaCelesteView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // coletando informações do tamanho tela de desenho
        width=getMeasuredWidth();
        height=getMeasuredHeight();

        // definindo o raio da esfera celeste
        if (width<height)
            r=(int)(width/2*0.9);
        else
            r=(int)(height/2*0.9);
        // configurando o pincel para desenhar a projeção da esfera celeste
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        // desenha a projeção da esfera celeste
        // desenhando círculos concêntricos
        int radius=r;
        canvas.drawCircle(computeXc(0), computeYc(0), radius, paint);
        radius=(int)(radius*Math.cos(Math.toRadians(45)));
        canvas.drawCircle(computeXc(0), computeYc(0), radius, paint);
        radius=(int)(radius*Math.cos(Math.toRadians(60)));
        canvas.drawCircle(computeXc(0), computeYc(0), radius, paint);

        //desenhando os eixos
        canvas.drawLine(computeXc(0),computeYc(-r),computeXc(0),computeYc(r),paint);
        canvas.drawLine(computeXc(-r),computeYc(0),computeXc(r),computeYc(0),paint);

        int CONSTELLATION_GALILEO;

        int CONSTELLATION_GLONASS;

        int CONSTELLATION_GPS;
        // desenhando os satélites (caso exista um GnssStaus disponível)
        if (newStatus!=null) {
            for(int i=0;i<newStatus.getSatelliteCount();i++) {
                float az=newStatus.getAzimuthDegrees(i);
                float el=newStatus.getElevationDegrees(i);

            }
        }
    }
    private int computeXc(double x) {
        return (int)(x+width/2);
    }
    private int computeYc(double y) {
        return (int)(-y+height/2);
    }
    public void setNewStatus(GnssStatus newStatus) {
        this.newStatus=newStatus;
        invalidate();
    }
}
