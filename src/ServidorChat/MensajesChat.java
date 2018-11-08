
package ServidorChat;

import java.util.Observable;

public class MensajesChat extends Observable{

    private String mensaje;
    
    public MensajesChat(){
    }
    
    public String getMensaje(){
        return mensaje;
    }
    
    public void setMensaje(String mensaje){
        this.mensaje = mensaje;
        this.setChanged();
        this.notifyObservers(this.getMensaje());
    }
}