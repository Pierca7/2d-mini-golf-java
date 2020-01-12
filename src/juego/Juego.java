package juego;


import entorno.Entorno;
import entorno.InterfaceJuego;
import lvl.Generador;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Juego extends InterfaceJuego
{
	// El objeto Entorno que controla el tiempo y otros
	private Entorno entorno;
	
	// Variables y mÃ©todos propios de cada grupo
	private Bola bola;
	private Generador generador;	
	private Controles controles;
	private Nivel nivel;
	private Image menuImg = null;
	
	private boolean menu;	//Esta en el menu?
	private int[] posMenu; //Posicion de la flecha en el menu
	private boolean terminoTurno; // Termino el turno?
	private int nivelActual; //Numero de nivel
	private int ticks;	//Numero de ticks
	private static int golpes; // Numero de golpes
	
	//Golpes++
	static void incGolpes(){
		golpes++;
	}
	
	//GEtter de nivel
	public int getNivel() {
		return this.nivelActual;
	}
	
	//Genera un numero aletorio dentro de un rango.
	public static int generarRandom(int min, int max, int step){
		int rand = (int)( Math.random() * (max-min+1) );
		return rand - rand%step + min;
	}		
	
	//Revisa si existe el nivel.
	private boolean existeNivel() {
		File img = new File("src/lvl/lvl" + nivelActual + ".png");
		File data = new File("src/lvl/lvl" + nivelActual + ".txt");
		if (img.isFile() && data.isFile()) {
			return true;
		}
		return false;
	}
	
	//Actualiza la velocidad
	private void actualizarVel() {
		bola.setVelX(controles.getPotencia(),controles.getAngulo());
		bola.setVelY(controles.getPotencia(),controles.getAngulo());
	}
	
	//Resetea todo al final del turno
	private void finDelTurno() {
		this.terminoTurno  = false;
		bola.setVelX(0,0);
		bola.setVelY(0,0);
		controles.setPotencia(0);
		controles.resetSolto();
	}
	
	//Resetea todo al final del nivel
	private void finDelNivel() {
		this.terminoTurno  = false;
		bola.setVelX(0,0);
		bola.setVelY(0,0);
		controles.setPotencia(0);
		controles.resetSolto();
		this.nivelActual++;	
		this.ticks = -1;
		golpes = 0;
	}
	 
	Juego()
	{
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Prueba del entorno", 800, 640);

		// Inicializar lo que haga falta para el juego
		try {
			menuImg = ImageIO.read(new File("src/img/menu.png"));
			}
		catch (IOException e){
			e.printStackTrace();
		}		
		menu = true;
		nivelActual=1;
		ticks = 0;
		generador = new Generador(generarRandom(4,8,1));
		controles = new Controles();
		terminoTurno = false;	
		
		// Inicia el juego!
		this.entorno.iniciar();
	}

	// Procesamiento de un instante de tiempo.
	public void tick()	{
		//En el menu
		if (menu) {
			entorno.dibujarImagen(menuImg, entorno.ancho()/2, entorno.alto()/2, 0);
			posMenu = controles.setPosMenu(entorno);
			entorno.dibujarTriangulo(posMenu[0], posMenu[1], 40, 20, 0, Color.BLACK);
			menu = controles.opciones(entorno);
			controles.salir(entorno);
		}
		//Saliendo del menu
		if(!menu) {				
			//Si no existe el nivel, se crea. Si existe, se cargan los datos.
			if (ticks<1) {
				if (existeNivel()) {
					nivel = new Nivel(nivelActual);
				}
				else{
					generador.generarLvl(entorno);
					generador.dibujarLvl(entorno);
				}
				controles.salir(entorno);				
			}
			//Si se creo el nivel en el tick anterior, en este se cargan los datos.
			if (ticks==1) {
				if(!existeNivel()){
					generador.crearLvl(entorno, this);
					nivel = new Nivel(nivelActual);
				}
				entorno.dibujarImagen(nivel.getImagen(), entorno.ancho()/2, entorno.alto()/2, 0);	
				controles.salir(entorno);
			}
			//Se crea una nueva bola
			if (ticks == 2) bola = new Bola(8,40,nivel); 
			
			if (ticks >= 2) {
				
				//Dibujado del nivel
				entorno.dibujarImagen(nivel.getImagen(), entorno.ancho()/2, entorno.alto()/2, 0);
				entorno.cambiarFont("Microsoft JhengHei UI", 20, Color.WHITE);
				entorno.escribirTexto("Golpes: "+golpes, 16, 26);
				entorno.escribirTexto("Nivel "+nivelActual, 720, 26);
				
				//Configuracion del golpe
				controles.girarFlecha(entorno, bola);
				controles.elegirPotencia(entorno, bola);	
				
				//Golpe
				if (controles.getSolto() && !terminoTurno ){ 
					actualizarVel();
					terminoTurno = true;	
				}
				
				//Guardado de la posicion anterior(para el agua)
				if(ticks==bola.getContador()) {
					bola.setPosAnterior(bola.getPosX(), bola.getPosY());
				}
				
				//Mover la bola
				bola.mover(entorno);
				
				//Evaluar si encontroi un obstaculo
				bola.chequearPosicion(entorno);
				
				//Si la pelota entra
				if(nivel.entro(bola.getPosX(), bola.getPosY())) {
					golpes++;
					finDelNivel();
				}
				
				//Suma el golpe y termina el turno
				if(bola.getVelX()==0 && bola.getVelY()==0 && terminoTurno){
					golpes++;
					finDelTurno();	
				}
				
				bola.setContador();
				controles.salir(entorno);
			}
			ticks++;
		}
	}
	

	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Juego juego = new Juego();
	}
}

