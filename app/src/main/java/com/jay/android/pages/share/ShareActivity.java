package com.jay.android.pages.share;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.jay.android.R;
import com.jay.android.base.BaseActivity;
import com.jay.android.utils.ScreenUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ShareActivity extends BaseActivity {

    public final static String SHARE_URL = "share_url";

    @Override
    public int getContentViewId() {
        return R.layout.activity_share;
    }

    @Override
    public String getPageTitle() {
        return getString(R.string.title_share);
    }

    @Override
    public boolean isShowFab(){
        return false;
    }
    @Override
    public boolean isShowMenu(){
        return false;
    }
    @Override
    public void initView() {
        ImageView share = findViewById(R.id.iv_share);
        String url = getIntent().getStringExtra(SHARE_URL);
        try {
            int w = ScreenUtil.getScreenWidth(this);
            share.setImageBitmap(getQEncode(url,w));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Bitmap getQEncode(String Value, int QRcodeWidth) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}
