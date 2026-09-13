package pt.loualmeida.lojavirtual.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatadorUtils {

    public static String formataValorMonetario(Double valor) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        return format.format(valor);
    }
}
