package juego;

import entorno.Entorno;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bola {
	
	Bola(int posX, int posY, Nivel nivel){
		this.posicion[0] = posX;
		this.posicion[1] = posY;
		this.nivel = nivel;
	}
	
	private double[] posicion = new double[2];
	private double[] posicionAnterior = new double[2];
	private Nivel nivel;
	private double velX = 0; //velX
	private double velY = 0; //velY
	private double aceleracion = 0.98;
	private int contador = 2;
	private File loc = new File("src/img/pelota.png");
	private Image Imagen = null;{	
	try {
		Imagen = ImageIO.read(loc);
		}
	catch (IOException e){
	}}
	
	//Getter de contador
	int getContador(){
		return contador;
	}
	
	//contador++
	void setContador(){
		contador++;
	}
	
	//Getter de velX
	double getVelX(){
		return this.velX;
	}
	
	//Getter de velY
	double getVelY(){
		return this.velY;
	}	
	
	// Setea la velX
	void setVelX(double a, double ang){		
		double ratio = Math.cos(ang);
		this.velX = a*0.2*ratio;
	}
	
	//Seta la VelY
	void setVelY(double a, double ang){	
		double ratio = Math.sin(ang);
		this.velY = a*0.2*ratio;
	}
	
	//Getter de posX
	double getPosX() {
		return this.posicion[0];
	}
	
	//Getter de posY
	double getPosY() {
		return this.posicion[1];
	}
	
	//Getter de la posAnteriorX
	double getPosAntX() {
		return this.posicionAnterior[0];
	}
	
	//Getter de la posAnteriorY
	double getPosAntY() {
		return this.posicionAnterior[1];
	}
	
	//Getter de la pos anterior
	void setPosAnterior(double x, double y){
		this.posicionAnterior[0] = x;	
		this.posicionAnterior[1] = y;	
	}
	
	//Mueve la bola
	void mover(Entorno entorno) {
		//Suma la velocidad a la posicion
		this.posicion[0] = (this.posicion[0] + this.velX);
		this.posicion[1] = (this.posicion[1] + this.velY);
		//Disminuye la velocidad por tick
		this.velX = this.velX * this.aceleracion;
		this.velY = this.velY * this.aceleracion;
		//Si llega al borde, invierte la velocidad
		if(this.posicion[0] < 8 ) {
			this.posicion[0] = 8;
			this.velX = -this.velX;
			}
		else if (this.posicion[0] > entorno.ancho()-8) {
			this.posicion[0] = entorno.ancho()-8;
			this.velX = -this.velX;
		}			
		if(this.posicion[1] < 40 ) {
			this.posicion[1] = 40;
			this.velY = -this.velY;
			}
		else if (this.posicion[1] > entorno.alto()-8) {
			this.posicion[1] = entorno.alto()-8;
			this.velY = -this.velY;
		}
		
		//Si la velocidad es muy chica, la settea a 0
		if(Math.abs(this.velX) < 0.05)
			velX = 0;
		if(Math.abs(this.velY) < 0.05)
			velY = 0;
		//Deja la posicion final en un numero entero
		if (this.velX == 0 && this.velY == 0) {
			this.posicion[0] = (int) this.posicion[0];
			this.posicion[1] = (int) this.posicion[1];
		}
		
		entorno.dibujarImagen(this.Imagen, this.posicion[0], this.posicion[1], 0);
	}	
	
	//Ralentiza la bola si esta en arena
	private void enArena() {
		this.velX *= 0.9;
		this.velY*= 0.9;
	}
	
	//Saca la pelota del agua y suma un golpe
	private void enAgua() {
		this.velX = 0;
		this.velY = 0;
		this.posicion[0]=posicionAnterior[0];
		this.posicion[1]=posicionAnterior[1];
		Juego.incGolpes();
	}
	
	//Aumenta la velocidad de la bola
	private void enDash() {
			this.velX*=1.2;
			this.velY*=1.2;
	}
	
	//Teletransporta la bola a un lugar aleatorio
	private void enTeleport(Entorno entorno) {
		double[] aux = new double[2];
		do {
			aux[0] = Juego.generarRandom(8, entorno.ancho()-8, 1);
			aux[1] = Juego.generarRandom(40, entorno.alto()-8, 1);
		}while (nivel.estaEnAgua(aux[0], aux[1]));
		this.posicion = aux;
	}
	
	//Hace rebotar la bola
	private void enTronco() {
		this.velX*=-1;
		this.velY*=-1;
	}
	
	//Chequea si colisiono contra algun obstaculo.
	void chequearPosicion(Entorno entorno) {
		if (nivel.estaEnAgua(getPosX(), getPosY())) enAgua();
		else if (nivel.estaEnArena(getPosX(), getPosY())) enArena();
		else if (nivel.estaEnDash(getPosX(), getPosY()) ) enDash();
		else if (nivel.estaEnTeleport(getPosX(), getPosY()) ) enTeleport(entorno);
		else if (nivel.estaEnTronco(getPosX(), getPosY())) enTronco();
	}
	
}
