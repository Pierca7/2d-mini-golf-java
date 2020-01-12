package lvl;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

import entorno.Entorno;
import juego.Juego;


public class Generador {
	
	private List<Obstaculo> Obstaculos = new ArrayList<Obstaculo>(); //Lista de obstaculos
	private List<Integer[]> Troncos = new ArrayList<Integer[]>(); //Lista de troncos
	private List<Integer[]> Teleport = new ArrayList<Integer[]>(); // Lista de teleport
	private List<Integer[]> Dash = new ArrayList<Integer[]>();	//lista de dash.
	private int cantObstaculos; //Cantidad de obstaculos a generar.
	private String[] tipos = {"arena_","agua_"}; //Tipos de obstaculos validos. Se usa para elegir un de forma aleatoria
	private Image fondo = null;{ //Imagen del cesped.
	try {
		fondo = ImageIO.read(new File("src/img/fondo.png"));
	} catch (IOException e) {
		e.printStackTrace();
	}}

	//Obstaculos esta vacio?
	private boolean isEmpty() {		
		return Obstaculos.isEmpty();
	}
	
	//Mueve el obstaculo hacia su posicion final.
	private void moverPosiciones(List<Integer[]> posiciones, Obstaculo obstaculo, int offsetX, int offsetY ) {
		for (int i = 0 ; i < posiciones.toArray().length; i++) {
			Integer[] casilla = posiciones.get(i);
			casilla[0] = casilla[0] + offsetX;
			casilla[1] = casilla[1] + offsetY;
			obstaculo.casillas.add(casilla);	
		}
		Obstaculos.add(obstaculo);
	}
	
	//Verifica que la posicion este libre revisando si las zonas intersectan.
	private void verificarInterseccion(Entorno entorno, Rectangle zona, List<Integer[]> lista, int f) {
		for (int k = 0 ; k<lista.toArray().length ; k++) {
			Rectangle zona2 = new Rectangle(lista.get(k)[0]-32,lista.get(k)[1]-32,64,64);
			if(zona.intersects(zona2)) f=1;
		}
	}
	
	//Genera los elementos.Es la base de cada funcion individual(troncos, dash y teleport)
	private void generarElementos( Entorno entorno, int cant,List<Integer[]> lista) {
		boolean esValido = false;
		int x=0, y=0, f=0, cont; 
			cont = 0;
			esValido = false;
			//Elige una posicion al azar y verifica que no este ocupada hasta un maximo de 5 veces.
			for (int u = 0 ; u<cant ; u++) {
				esValido = false;
				f=0;
				while (!esValido && cont<5) {
					x = Juego.generarRandom(24 , entorno.ancho()-24 , 1);
					y = Juego.generarRandom(58 , entorno.alto()-24 , 1);
					Rectangle zona = new Rectangle(x-32,y-32,64,64);
					
					for (int j = 0 ; j<Obstaculos.toArray().length ; j++) {
						if (Obstaculos.get(j).getZona().intersects(zona)) {
							f=1;
						}
					}
					verificarInterseccion(entorno,zona,Teleport,f);
					verificarInterseccion(entorno,zona,Dash,f);
					verificarInterseccion(entorno,zona,Troncos,f);
					if(f==0) {
						esValido = true;	
					}
					//Si elige una posicion valida, agrega el elemento.
					if(esValido)lista.add(new Integer[]{x,y});
					cont++;
				}
			}
	}
	
	//Dibuja los elementos. Es la base de cada funcion individual(troncos, dash y teleport)
	private void dibujarElementos(Entorno entorno, String nombre, List<Integer[]> lista) {
		Image imagen = null;
		try {
			imagen = ImageIO.read(new File("src/img/" + nombre + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0 ; i<lista.toArray().length ; i++) {
			entorno.dibujarImagen(imagen, lista.get(i)[0], lista.get(i)[1], 0);
		}
	}
	
	//Genera los troncos.
	private void generarTroncos(Entorno entorno) {
		int cant = Juego.generarRandom(8, 16, 1);
		generarElementos( entorno, cant, Troncos);
	}
	
	//Dibuja los troncos.
	private void dibujarTroncos(Entorno entorno) {
		dibujarElementos(entorno, "tronco_", Troncos);
	}
	
	//Genera los teleport.
	private void generarTeleport(Entorno entorno) {
		int cant = Juego.generarRandom(8, 16, 1);
		generarElementos( entorno, cant, Teleport);
	}
	
	//Dibuja los teleport.
	private void dibujarTeleport(Entorno entorno) {
		dibujarElementos(entorno, "teleport_", Teleport);
	}
	
	//Genera los dash.
	private void generarDash(Entorno entorno) {
		int cant = Juego.generarRandom(8, 16, 1);
		generarElementos( entorno, cant, Dash);
	}
	
	//Dibuja los dash.
	private void dibujarDash(Entorno entorno) {
		dibujarElementos(entorno, "dash_", Dash);
	}
	
	//Dibuja el agujero.
	private void dibujarAgujero(Entorno entorno) {
		Image aguj = null;
		try {
			aguj = ImageIO.read(new File("src/img/agujero.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		entorno.dibujarImagen(aguj, 792, 632,0);
	}

	//Genera los obstaculos.
	private void generarObstaculo(String tipo, Entorno entorno) {
		int offsetX = 0 , offsetY = 0;
		int alto = Juego.generarRandom(4,8,1) , ancho = Juego.generarRandom(4,8,1);		
		Obstaculo obstaculo = new Obstaculo(alto,ancho,tipo);	
		
		//Crea una lista con las posiciones donde se dibujara el obstaculo, con origen en 0,0.
		List<Integer[]> posiciones = new ArrayList<Integer[]>();
		for(int i = 0 ; i<alto*16 ; i+=16) {
			for (int j = 0; j<ancho*16; j+=16) {
				posiciones.add(new Integer[] {j,i});
			}
		}			
		//Si es el primer obstaculo creado, genera un despazlazamiento en X e Y y se lo suma a las posiciones de origen para obtener su posicion final
		if (isEmpty()) {
			offsetX = Juego.generarRandom( (16*(int)(ancho/2)+16) , (entorno.ancho()-(16*ancho))-16 , 1);
			offsetY = Juego.generarRandom( (16*(int)(alto/2)+48) , (entorno.alto()-(16*alto))-16 , 1);
			obstaculo.generarZona(offsetX, offsetY);
			moverPosiciones(posiciones , obstaculo , offsetX , offsetY);
		}	
		//Si no es el primer obstaculo creado, hace lo miso que el anterior pero ademas verifica que no se superponga con otro obstaculo.
		//Si se superpone, genera un nuevo desplazamiento hasta un maximo de 5 veces (para evitar un loop infinito en caso de que no haya mas espacio disponible)
		else {
			int f = 0, ciclos = 0;
			boolean posValida = false;			
			while(!posValida && ciclos<5) {			
				offsetX = Juego.generarRandom(16*(int)(ancho/2)+16 , (entorno.ancho()-(16*ancho))-16 , 1);
				offsetY = Juego.generarRandom(16*(int)(alto/2)+48 , (entorno.alto()-(16*alto))-16 , 1);
				obstaculo.generarZona(offsetX, offsetY);
				for (int i = 0 ; i<Obstaculos.toArray().length ; i++) {
					if (obstaculo.getZona().intersects(Obstaculos.get(i).getZona())) {
						f=1;
					}				
				}				
				if (f==0) posValida=true;
				f=0;
				ciclos++;
			}
			//Si se encontro un posicion valido, se mueve el obstaculo hacia su posicion final
			if (posValida)moverPosiciones(posiciones , obstaculo , offsetX , offsetY);
		}	
	}
	
	//Dibuja los obstaculos.
	private void dibujarObstaculos(Entorno entorno) {
		for (int i = 0; i<Obstaculos.toArray().length ; i++) {
			Obstaculo obs = Obstaculos.get(i);			
			for (int j = 0 ; j<obs.casillas.toArray().length ; j++) {
				Integer[] aux = obs.casillas.get(j);
				
				//En base a las coordenadas, decide de esa cxasilla es un borde, una esquina o o parte del medio y elige la imagen a usar
				if(aux[0] == obs.getX() && aux[1] == obs.getY()) obs.setImagen(obs.getTipo(), "siz");
				else if(aux[0] == obs.getX() && aux[1] == obs.getY()+16*obs.getAlto()-16) obs.setImagen(obs.getTipo(), "iiz");
				else if(aux[0] == obs.getX()+16*obs.getAncho()-16 && aux[1] == obs.getY()) obs.setImagen(obs.getTipo(), "sde");
				else if(aux[0] == obs.getX()+16*obs.getAncho()-16 && aux[1] == obs.getY()+16*obs.getAlto()-16) obs.setImagen(obs.getTipo(), "ide");
				else if(aux[0] == obs.getX()) obs.setImagen(obs.getTipo(), "iz");
				else if(aux[0] == obs.getX()+16*obs.getAncho()-16) obs.setImagen(obs.getTipo(), "de");
				else if(aux[1] == obs.getY()) obs.setImagen(obs.getTipo(), "s");
				else if(aux[1] == obs.getY()+16*obs.getAlto()-16) obs.setImagen(obs.getTipo(), "i");		
				else obs.setImagen(obs.getTipo(), "");
				//Dibuja la imagen
				entorno.dibujarImagen( obs.getImagenActual() , aux[0] , aux[1] , 0 );
			}
		}
	}
	
	//Escribe los obstaculos
	private void escribirObstaculos(PrintWriter writeText , String nombre , List<Integer[]> lista) {
		writeText.print(nombre + "\n");
		for (int i = 0 ; i<lista.toArray().length ; i++) {
			String aux = lista.get(i)[0].toString() + "," +  lista.get(i)[1].toString() + "," + lista.get(i)[2].toString() + "," + lista.get(i)[3].toString() + "\n";
			writeText.print(aux);
		}
    }
	
	//Genera las colisiones de los elementos.
	private void generarColisiones(List<Integer[]> lista, List<Integer[]> listaCol){
		for (int i = 0 ; i<lista.toArray().length ; i++) {
			Integer[] aux = new Integer[4];
			aux[0] = lista.get(i)[0] -16;
			aux[1] = lista.get(i)[1] -16;
			aux[2] = 32;
			aux[3] = 32;	
			listaCol.add(aux);
		}
		
	}

	//Genera el nivel.
	public void generarLvl(Entorno entorno) {
		Obstaculos = new ArrayList<Obstaculo>();
		Teleport = new ArrayList<Integer[]>();
		Dash = new ArrayList<Integer[]>();
		Troncos = new ArrayList<Integer[]>();
		for (int i = 0 ; i<this.cantObstaculos ; i++ ) {
			int index = Juego.generarRandom(0,tipos.length-1,1);
			generarObstaculo(tipos[index],entorno);
		}
		generarTroncos(entorno);
		generarTeleport(entorno);
		generarDash(entorno);
	}

	//Dibuja el nivel
	public void dibujarLvl(Entorno entorno) {
		entorno.dibujarImagen(fondo, entorno.ancho()/2, (entorno.alto()+32)/2, 0);
		entorno.dibujarRectangulo(entorno.ancho()/2, 16, entorno.ancho(), 32, 0, Color.BLACK);
		dibujarAgujero(entorno);
		dibujarObstaculos(entorno);
		dibujarTroncos(entorno);
		dibujarTeleport(entorno);
		dibujarDash(entorno);
	}	
	
	/*Funcion que crea los archivos del nivel. Creara una captura de pantalla del mismo que luego el juego dibujara,
	ademas de un archivo de texto con las posiciones de los obstaculos para luego generar las colisiones*/
	public void crearLvl(Entorno entorno, Juego juego){
	
	//Listas donde se guardan las posiciones de los obstaculos y ayudas.
	List<Integer[]> posArena = new ArrayList<Integer[]>();
	List<Integer[]> posAgua = new ArrayList<Integer[]>();
	List<Integer[]> posTeleport = new ArrayList<Integer[]>();
	List<Integer[]> posDash = new ArrayList<Integer[]>();
	List<Integer[]> posTroncos = new ArrayList<Integer[]>();
	
	//Nombre del archivo de nivel a generar.
	String archivo = "lvl" + juego.getNivel() + ".txt";	
	//Posicion de origen de entorno
	Point pos = ((JComponent)entorno.getContentPane()).getLocationOnScreen();
	//Captura de pantalla del nivel.
	BufferedImage image = null;
	try {
		image = new Robot().createScreenCapture( new Rectangle( pos.x, pos.y , entorno.ancho(), entorno.alto() ) );
	} catch (AWTException e) {
		e.printStackTrace();
	}
	//Directorio donde se guardara la imagen del nivel.
	File file = new File("src/lvl/lvl" + juego.getNivel() + ".png");
	
	//Guardar imagen.
	try {
		ImageIO.write(image, "png", file);
	} catch (IOException e) {
		e.printStackTrace();
	}
	//Clase que escribe los archivos de texto.
	PrintWriter writeText = null;
	try {
		writeText = new PrintWriter(new File("src/lvl/"+archivo), "UTF-8");
	} catch (FileNotFoundException | UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	
	//Recorre la lista de obstaculos revisando sus tipos, y copia sus posiciones a las listas correspondientes.
	for (int i = 0 ; i<Obstaculos.toArray().length ; i++) {
		Integer[] aux = new Integer[4];
		aux[0] = Obstaculos.get(i).getX()-8;
		aux[1] = Obstaculos.get(i).getY()-8;
		aux[2] = 16*Obstaculos.get(i).getAncho();
		aux[3] = 16*Obstaculos.get(i).getAlto();
		
		if(Obstaculos.get(i).getTipo() == "arena_" ) {
			posArena.add(aux);			
		}
		else if(Obstaculos.get(i).getTipo() == "agua_" ) {
			posAgua.add(aux);			
		}
	}
	
	//Genera los datos de los rectangulos de colision de los elementos
	generarColisiones(Teleport,posTeleport);
	generarColisiones(Dash,posDash);
	generarColisiones(Troncos,posTroncos);	
	
	//Escribe los datos en el archivo.
	escribirObstaculos(writeText, "posArena", posArena);
	escribirObstaculos(writeText, "posAgua", posAgua);
	escribirObstaculos(writeText, "posTeleport", posTeleport);
	escribirObstaculos(writeText, "posDash", posDash);
	escribirObstaculos(writeText, "posTroncos", posTroncos);	
	writeText.write("agujero=\n" + 792 + "," + 632 + "\n" );
    writeText.close();
	
	}
	
	//Constructor
	public Generador(int cantObstaculos) {
		this.cantObstaculos = cantObstaculos;

	}



}
