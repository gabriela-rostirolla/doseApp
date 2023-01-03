package com.example.doseapp.classes;

import android.widget.Toast;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Criptografia {

    public static String encode(String mensagem, String chave) {
        if (mensagem.length() != chave.length())
            return "O tamanho da mensagem e da chave devem ser iguais.";
        int[] im = charArrayToInt(mensagem.toCharArray());
        int[] ik = charArrayToInt(chave.toCharArray());
        int[] data = new int[mensagem.length()];

        for (int i=0;i<mensagem.length();i++) {
            data[i] = im[i] + ik[i];
        }

        return new String(intArrayToChar(data));
    }

    public static String decriptografa(String mensagem, String chave) {
        if (mensagem.length() != chave.length())
            error("O tamanho da mensagem e da chave devem ser iguais.");
        int[] im = charArrayToInt(mensagem.toCharArray());
        int[] ik = charArrayToInt(chave.toCharArray());
        int[] data = new int[mensagem.length()];

        for (int i=0;i<mensagem.length();i++) {
            data[i] = im[i] - ik[i];
        }
        return new String(intArrayToChar(data));
    }

    public static String genKey(int tamanho) {
        Random randomico = new Random();
        char[] key = new char[tamanho];
        for (int i=0;i<tamanho;i++) {
            key[i] = (char) randomico.nextInt(132);
            if ((int) key[i] < 97) key[i] = (char) (key[i] + 72);
            if ((int) key[i] > 122) key[i] = (char) (key[i] - 72);
        }

        return new String(key);
    }

    public static int chartoInt(char c) {
        return (int) c;
    }

    public static char intToChar(int i) {
        return (char) i;
    }
    public static int[] charArrayToInt(char[] cc) {
        int[] ii = new int[cc.length];
        for(int i=0;i<cc.length;i++){
            ii[i] = chartoInt(cc[i]);
        }
        return ii;
    }

    public static char[] intArrayToChar(int[] ii) {
        char[] cc = new char[ii.length];
        for(int i=0;i<ii.length;i++){
            cc[i] = intToChar(ii[i]);
        }
        return cc;
    }

    public static void error(String msg) {
        System.out.println(msg);
        System.exit(-1);
    }
}
