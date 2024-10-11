package com.example.lbs;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SatelliteSignal extends View {

    private Paint paint;
    private int[] svid = {1, 2, 3, 4, 5};  // IDs dos satélites
    private int[] snr = {30, 50, 40, 20, 60};  // SNR dos satélites

    public SatelliteSignal(Context context) {
        super(context);
        init();
    }

    public SatelliteSignalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SatelliteSignal(Context context) {
        super(context);
    }

    private void init() {
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Define o fundo branco
        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLUE);
        paint.setTextSize(50);
        paint.setStrokeWidth(10);

        // Desenhar barras para cada satélite
        for (int i = 0; i < svid.length; i++) {
            int left = 100;
            int top = 100 + i * 150;
            int right = left + snr[i] * 10; // O comprimento da barra é proporcional ao SNR
            int bottom = top + 100;

            // Desenha a barra do sinal
            paint.setColor(Color.GREEN);
            canvas.drawRect(left, top, right, bottom, paint);

            // Desenha o texto com o número de satélite (SVID)
            paint.setColor(Color.BLACK);
            canvas.drawText("Satélite " + svid[i], left, top - 20, paint);
        }
    }
}