package com.example.labremotoclp;

import java.util.LinkedHashMap;
import java.util.Map;

public class Interface {
    public final static int STATUS_DESABILITADO = 0;
    public final static int STATUS_ATIVADO = 1;

    // Input pins: 36, 39, 34, 35, 14, 12, 5, 13, 2, 15
    private final static String[] INPUT_KEYS = {
        "Input_36", "Input_39", "Input_34", "Input_35", "Input_14", 
        "Input_12", "Input_5", "Input_13", "Input_2", "Input_15"
    };

    private final static String[] INPUT_NAMES = {
        "Válvula 1", "Válvula 2", "Resistência", "Misturador", "Alarme Temp.",
        "Status Operação", "Vál. Esvaziar", "IN 8", "IN 9", "IN 10"
    };

    // Output pins: 4, 19, 18, 17, 27, 23, 26, 25, 33, 32
    private final static String[] OUTPUT_KEYS = {
        "Output_4", "Output_19", "Output_18", "Output_17", "Output_27",
        "Output_23", "Output_26", "Output_25", "Output_33", "Output_32"
    };

    private final static String[] OUTPUT_NAMES = {
        "Liga", "Desliga", "Termostato", "Emergência", "Esvaziar",
        "OUT 6", "OUT 7", "OUT 8", "OUT 9", "OUT 10"
    };

    private final static Map<String, Object> statusInterfaces = new LinkedHashMap<>();

    static {
        for (String key : INPUT_KEYS) statusInterfaces.put(key, STATUS_DESABILITADO);
        for (String key : OUTPUT_KEYS) statusInterfaces.put(key, STATUS_DESABILITADO);
        statusInterfaces.put("Temp_A0", 0.0f);
        statusInterfaces.put("Temp_A1", 0.0f);
        statusInterfaces.put("Temp_A2", 0.0f);
        statusInterfaces.put("Temp_A3", 0.0f);
        statusInterfaces.put("DAC_Ch0", 0.0f);
        statusInterfaces.put("DAC_Ch1", 0.0f);
        statusInterfaces.put("DAC_Ch2", 0.0f);
        statusInterfaces.put("DAC_Ch3", 0.0f);
        // Inverter Status
        statusInterfaces.put("inv_f", 0.0f);
        statusInterfaces.put("inv_r", 0);
        statusInterfaces.put("inv_a", 0.0f);
        statusInterfaces.put("inv_s", "AVANCO");
        statusInterfaces.put("inv_w", "0x0000");
        statusInterfaces.put("inv_w_nome", "---");
        // Diagnostics
        statusInterfaces.put("wifi", 0);
        statusInterfaces.put("heap", 0);
    }

    public static void atualizarInterface(String nomeInterface, Object valor){
        statusInterfaces.put(nomeInterface, valor);
    }

    public static String[] getInputKeys() { return INPUT_KEYS; }
    public static String[] getOutputKeys() { return OUTPUT_KEYS; }
    public static String[] getInputNames() { return INPUT_NAMES; }
    public static String[] getOutputNames() { return OUTPUT_NAMES; }

    public static String converteParaJson(){
        StringBuilder retorno = new StringBuilder("{");
        int cont = 0;
        for (Map.Entry<String, Object> entry : statusInterfaces.entrySet()) {
            retorno.append("\"").append(entry.getKey()).append("\"").append(":");
            if (entry.getValue() instanceof String) {
                retorno.append("\"").append(entry.getValue()).append("\"");
            } else {
                retorno.append(entry.getValue());
            }
            cont++;
            if(cont != statusInterfaces.size()) {
                retorno.append(",");
            }
        }
        retorno.append("}");
        return retorno.toString();
    }
}
