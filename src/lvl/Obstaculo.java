package lvl;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Obstaculo {
	List<Integer[]> casillas; //Lista de posiciones que ocupa el obstaculo.
	
	private int alto; //Alto en tiles del obstaculo.
	private int ancho; //Ancho en tiles del obstaculo.
	private String tipo; //Tipo del obstaculo.
	private Rectangle zona;	//Rectangulo invisible que envuelve al obstaculo. Es usado para calcular la posicion de cada obstaculo y evitar que se generen varios en el mismo lugar
	
	private String nombre;//Nombre de la imagen
	private File loc;//Directorio de la imagen
	private Image Imagen = null;//Imagen que se utiliza para contrnstruir el obstaculo. Cambia en los bordes del mismo.
	
	//Set del nombre de la imagen a utilizar.
	private void setNombre(String tip, String pos) {
			this.nombre = tip + pos + ".png";		
	}
	
	//Set de la imagen a utilizar para cada parte del obstaculo.
	void setImagen(String tip, String pos) {
		setNombre(tip,pos);
		loc = new File("src/img/" + nombre);
		try {
			Imagen = ImageIO.read(loc);
			}
		catch (IOException e){
		}
		
	}
		
	//Getter de ancho.
	int getAncho() {
		return this.ancho;
	}
	
	//Getter de alto.
	int getAlto(){
		return this.alto;
	}
	
	//Getter de tipo.
	String getTipo() {
		return this.tipo;
	}
	
	//Getter de la imagen actual.
	Image getImagenActual() {
		return this.Imagen;
	}
	
	//Getter de la posicion X de inicio del obstaculo.
	int getX(){
		return this.casillas.get(0)[0];
	}
	
	//Getter de la posicion Y de inicio del obstaculo.
	int getY(){
		return this.casillas.get(0)[1];
	}
	
	//Genera un rectangulo alededor del obstaculo que se usara para calcular su posicion con respecto a otros (que no se superpongan)
	void generarZona(int offsetX , int offsetY) {
		 int X = offsetX - 16;
		 int Y = offsetY - 16;
		 int width = 16*this.ancho + 16;
		 int height = 16*this.alto + 16;
		 this.zona = new Rectangle( X , Y , width , height );
	}
	
	//Getter de zona
	Rectangle getZona() {
		return this.zona;	
	}
	
	//Constructor de obstaculo:
	Obstaculo(int alto, int ancho , String tipo){
		this.alto = alto;
		this.ancho = ancho;
		this.tipo = tipo;
		casillas = new ArrayList<Integer[]>();
		setImagen(tipo,"");	
	}
}
