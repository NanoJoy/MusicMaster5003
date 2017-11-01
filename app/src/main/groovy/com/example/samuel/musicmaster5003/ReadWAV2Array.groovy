
package com.example.samuel.musicmaster5003

import android.app.Activity
import android.content.res.Resources
import org.apache.commons.io.IOUtils

import java.nio.ByteBuffer
import java.nio.ByteOrder

public class ReadWAV2Array {

    private byte[] entireFileData
    private double[] data
    private Activity activity
    private String fileInfo
    private int numChannels

    String getFileInfo() {
        return fileInfo
    }

    double getSamplingRate(){
        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(entireFileData, 24, 28))
        double samplingRate = wrapped.order(ByteOrder.LITTLE_ENDIAN).getInt()
        samplingRate
    }

    ReadWAV2Array(Activity activity, String fileName, boolean inCache, double[] data = null) throws IOException {
        this.data = data
        this.activity = activity
        InputStream inputStream
        if (inCache) {
            File file = new File(fileName)
            inputStream = new FileInputStream(file)
        } else {
            Resources resources = activity.getResources()
            inputStream = resources.openRawResource(
                    resources.getIdentifier(fileName, "raw", activity.getPackageName()))
        }
        entireFileData = IOUtils.toByteArray(inputStream)
        String format = new String(Arrays.copyOfRange(entireFileData, 8, 12), "UTF-8")

        numChannels = entireFileData[22]
        String noOfChannels_str
        if (numChannels == 2)
            noOfChannels_str = "2 (stereo)"
        else if (numChannels == 1)
            noOfChannels_str = "1 (mono)"
        else
            noOfChannels_str = "$noOfChannels (more than 2 channels)"

        int samplingRate = (int) this.getSamplingRate()

        int bitPerSecond = entireFileData[34]
        StringBuilder sbFileInfo = new StringBuilder()
        sbFileInfo.append("File format:        " + format)
         .append("\nNumber of channels: " + noOfChannels_str)
         .append("\nSampling rate:      " + samplingRate)
         .append("\nBit depth:          " + bitPerSecond)
        fileInfo = sbFileInfo.toString()
    }

    public double[] getByteArray (){
        if (data != null) {
            return data
        }
        byte[] data_raw = Arrays.copyOfRange(entireFileData, 44, entireFileData.length);
        int totalLength = data_raw.length;

        if (numChannels == 2) {

            int newLength = totalLength / 4;
            Double[] data_mono = new Double[newLength];

            double left, right;
            0.upto(newLength - 1, {
                left = (short)((data_raw[it * 4 + 1] & 0xff) << 8) | (data_raw[it * 4] & 0xff);
                right = (short)((data_raw[it * 4 + 3] & 0xff) << 8) | (data_raw[it * 4 + 2] & 0xff);
                data_mono[it] = (left + right) / 2.0;
            })
            return data_mono;
        }
        def dataMono = new double[totalLength / 2]
        for (int i = 0; i < dataMono.length; i ++) {
            dataMono[i] = (short) ((data_raw[i * 2 + 1] & 0xff) << 8) | (data_raw[i * 2] & 0xff)
        }
        dataMono
    }
}
