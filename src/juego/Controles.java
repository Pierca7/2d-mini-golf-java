package juego;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import entorno.Entorno;

public class Controles {
	
	private int[][] posMenu; //Guarda las coordenas donde se puede dibujar la fleecha
	private int seleccion = 0;//Opcion seleccionada
	private double angulo = 0;//Angulo del golpe
	private double potencia = 0;//Potencia del golpe
	private boolean solto = false; // True si eligio la potencia del disparo
	private File loc = new File("src/img/flecha.png" );
	private Image imagen = null;{
	try {
		imagen = ImageIO.read(loc);
		}
	catch (IOException e){
		e.printStackTrace();
	}}
	
	//Seleciona la opcion del menu
	int[] setPosMenu(Entorno entorno) {
		if (seleccion == 1 && (entorno.estaPresionada(entorno.TECLA_ARRIBA))){
			seleccion = 0;
		}
		else if (seleccion == 0 && (entorno.estaPresionada(entorno.TECLA_ABAJO))) {
			seleccion = 1;
		}
		return this.posMenu[seleccion];
	}
	
	//Actua segun la opcion seleccionada
	boolean opciones(Entorno entorno) {
		if (seleccion==0 && (entorno.estaPresionada(entorno.TECLA_ENTER))) {
			return false;
		}
		else if (seleccion==1 && (entorno.estaPresionada(entorno.TECLA_ENTER))) {
			System.exit(0);
		}
		return true;
	}
	
	//Getter de solto
	boolean getSolto(){
		return this.solto;
	}
	
	//Resetea solto
	void resetSolto(){
		this.solto = false;
	}
	
	//Getter de angulo
	double getAngulo(){
		return this.angulo;
	}
	
	//Setter de angulo
	void setAngulo(double a){
		if (a/Math.PI<=2 && a/Math.PI>=0){
			this.angulo = a;
		}
		if (angulo/3.14159>2){
			this.angulo=0;
		}
	}

	//Getter de potencia
	double getPotencia(){
		return this.potencia;
	}

	
	//Setter de potencia
	void setPotencia(double a){
		if (a<=100){
			this.potencia = a;
		}
	}
	
	void girarFlecha(Entorno entorno, Bola bola){
		if(bola.getVelX()==0 && bola.getVelY()==0){
			if (entorno.estaPresionada(entorno.TECLA_DERECHA)){
				this.setAngulo(this.angulo+=0.05);
			}
			
			if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA)){
				if(angulo>0){
					this.setAngulo(this.angulo-=0.05);
				}
				if(angulo<=0){
					this.setAngulo(this.angulo=Math.PI*2-0.1);
				}
			}
			entorno.dibujarImagen(this.imagen, bola.getPosX(), bola.getPosY(), this.angulo);
		}
	}
	
	//Elegir la potencia
	void elegirPotencia(Entorno entorno, Bola bola){
			float aux=1;
			if(entorno.estaPresionada(entorno.TECLA_ESPACIO) && !solto){
				this.setPotencia(potencia+1);
				aux-=potencia/100;
			}
			if(!entorno.estaPresionada(entorno.TECLA_ESPACIO) && this.potencia>0){
				if(this.potencia<5){
					this.potencia = 5;
				}
				this.solto=true;
			}
			if (potencia == 100) aux = 0;
			Color color = new Color(1f, aux, 0f);
			entorno.dibujarRectangulo(entorno.ancho()/2, 16, this.potencia*3, 10, 0, color);	
		}
	
	//Sale del juego
	void salir(Entorno entorno){
			if(entorno.estaPresionada(entorno.TECLA_DELETE)) System.exit(0);
		}

	Controles(){
		posMenu = new int[2][2];
		posMenu[0][0] = 230;
		posMenu[0][1] = 365;
		posMenu[1][0] = 230;
		posMenu[1][1] = 465;
	}
	
}


	
	
	

