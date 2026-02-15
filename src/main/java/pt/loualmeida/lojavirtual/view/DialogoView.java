package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class DialogoView {

    public static void sucesso(String mensagem) {
        mensagem(mensagem, NotificationVariant.LUMO_SUCCESS);

    }
    public static void erro(String mensagem) {
        mensagem(mensagem, NotificationVariant.LUMO_ERROR);
    }

    public static void aviso(String mensagem) {
        mensagem(mensagem, NotificationVariant.LUMO_WARNING);
    }
    private static void mensagem(String mensagem, NotificationVariant tipo) {
        Notification notification = new Notification(mensagem,5000);
        notification.addThemeVariants(tipo);
        notification.open();
    }
}
