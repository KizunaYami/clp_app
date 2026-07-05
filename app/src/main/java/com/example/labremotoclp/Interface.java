package com.example.labremotoclp;

import java.util.LinkedHashMap;
import java.util.Map;

public class Interface {
    public final static int STATUS_DESABILITADO = 0;
    public final static int STATUS_ATIVADO = 1;

    private final static Map<String,Integer> statusInterfaces = new LinkedHashMap<String,Integer>();

    static {
        statusInterfaces.put("IN1", STATUS_DESABILITADO);
        statusInterfaces.put("IN2", STATUS_DESABILITADO);
        statusInterfaces.put("IN3", STATUS_DESABILITADO);
        statusInterfaces.put("IN4", STATUS_DESABILITADO);
        statusInterfaces.put("IN5", STATUS_DESABILITADO);
        statusInterfaces.put("IN6", STATUS_DESABILITADO);
        statusInterfaces.put("IN7", STATUS_DESABILITADO);
        statusInterfaces.put("IN8", STATUS_DESABILITADO);
        statusInterfaces.put("IN9", STATUS_DESABILITADO);
        statusInterfaces.put("IN10",STATUS_DESABILITADO);
        statusInterfaces.put("A0", STATUS_DESABILITADO);
        statusInterfaces.put("A1", STATUS_DESABILITADO);
    }

    public static void atualizarInterface(String nomeInterface,Integer valor){
        if(!statusInterfaces.containsKey(nomeInterface)){
            throw new IllegalArgumentException("Interface " + nomeInterface + " não existe");
        }
        statusInterfaces.put(nomeInterface,valor);
    }

    public static String converteParaJson(){
        StringBuilder retorno = new StringBuilder("{");
        int cont = 0;
        for (Map.Entry<String, Integer> entry : statusInterfaces.entrySet()) {
            retorno.append("\"").append(entry.getKey()).append("\"").append(":");
            retorno.append(entry.getValue());
            cont++;
            if(cont != statusInterfaces.size()) {
                retorno.append(",");
            }
        }
        retorno.append("}");
        return retorno.toString();
    }
}
