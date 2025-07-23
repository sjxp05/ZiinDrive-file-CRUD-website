package com.example.ziindrive.util;

public class FileSizeFormatter {

    // 사이즈 계산 및 포맷 함수
    public static String format(long size) {

        double sizeDouble = (double) size;

        String siUnit = "";
        String[] units = { "K", "M", "G" };

        for (String e : units) {

            if (sizeDouble / 1024 >= 1) {

                sizeDouble /= 1024;
                siUnit = e;

            } else {
                break;
            }
        }

        if (sizeDouble == (long) sizeDouble) {
            return String.format("%d ", (long) sizeDouble) + siUnit + "B";

        } else {
            return String.format("%.1f ", sizeDouble) + siUnit + "B";
        }
    }
}
