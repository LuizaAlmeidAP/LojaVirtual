package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Notificacoes {

    private static void mostrar(String msg,
                                NotificationVariant variant,
                                VaadinIcon iconType) {

        Notification notification = new Notification();

        // 🔹 topo da tela
        notification.setPosition(Notification.Position.TOP_END);

        // 🔹 nunca fecha sozinho
        notification.setDuration(1000);

        Icon icon = iconType.create();

        Span texto = new Span(msg);

        Button fechar = new Button(VaadinIcon.CLOSE_SMALL.create(),
                e -> notification.close());
        fechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        HorizontalLayout layout = new HorizontalLayout(icon, texto, fechar);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.expand(texto); // ocupa espaço central

        notification.add(layout);
        notification.addThemeVariants(variant);


        notification.open();
    }

    public static void sucesso(String msg) {
        mostrar(msg,
                NotificationVariant.LUMO_SUCCESS,
                VaadinIcon.CHECK_CIRCLE);
    }

    public static void erro(String msg) {
        mostrar(msg,
                NotificationVariant.LUMO_ERROR,
                VaadinIcon.WARNING);
    }

    public static void aviso(String msg) {
        mostrar(msg,
                NotificationVariant.LUMO_WARNING,
                VaadinIcon.EXCLAMATION_CIRCLE);
    }
}
