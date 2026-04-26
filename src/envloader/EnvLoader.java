package envloader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader{
    private static Map<String, String> env= new HashMap<>();

    static{
        try{
            BufferedReader br = new BufferedReader(new FileReader("/home/eloimcolossette/Desenvolvimento/SistemaCompras/resources/.env"));
            String linha;

            while((linha = br.readLine()) != null){
                String[] partes = linha.split("=", 2);
                if (partes.length == 2){
                    env.put(partes[0], partes[1]);
                }
            }

            br.close();
        }catch(Exception e ){
            System.out.println("Erro ao carregar .env");
        }
    }

    public static String get(String chave){
        return env.get(chave);
    }
}