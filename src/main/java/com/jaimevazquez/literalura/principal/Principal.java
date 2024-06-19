package com.jaimevazquez.literalura.principal;

import com.jaimevazquez.literalura.model.*;
import com.jaimevazquez.literalura.repository.AutorRepository;
import com.jaimevazquez.literalura.repository.LibroRepository;
import com.jaimevazquez.literalura.service.ConsumoAPI;
import com.jaimevazquez.literalura.service.ConvierteDatos;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    LibroRepository libroRepository;
    AutorRepository autorRepository;
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraMenu(){

        int opcion = -1;
        while(opcion != 0){
            String menu = """
                ------------------------------
                
                Elija una opción a través de un número:
                1 - Buscar y agregar un libro por titulo a la BD
                2 - Listar libros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos en un determinado año
                5 - Listar libros por idioma
                
                0 - Salir
                
                ------------------------------
                """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Debes escribir un número\n");
            }
            teclado.nextLine();

            switch (opcion){
                case 1:
                    obtenerLibroWeb();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosPorAno();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Fin del programa");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private void obtenerLibroWeb(){
        System.out.println("Escribe el nombre del libro que deseas buscar y agregar:");
        String libroBuscado = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + libroBuscado.replace(" ", "%20"));
        DatosRespuesta datosRespuesta = conversor.obtenerDatos(json, DatosRespuesta.class);
        Optional<DatosLibro> datosLibro = datosRespuesta.resultados()
                .stream()
                .filter(b -> b.titulo().toLowerCase().contains(libroBuscado.toLowerCase()))
                .findFirst();
        if(datosLibro.isEmpty()){
            System.out.println("No se encontró un libro con ese titulo.");
            return;
        }
        Optional<Libro> libroEncontradoEnBD = libroRepository.findByTituloContainsIgnoreCase(libroBuscado);
        if(libroEncontradoEnBD.isPresent()){
            System.out.println(libroEncontradoEnBD.get());
            System.out.println("Este libro ya se encontraba en la base de datos");
            return;
        }
        Libro libro = new Libro(datosLibro.get());
        List<DatosAutor> listaDatosAutores = datosLibro.get().autores();
        for(DatosAutor datosAutor : listaDatosAutores){
            Optional<Autor> autorEncontradoEnBD = autorRepository.findByNombreContainsIgnoreCase(datosAutor.nombre());
            if(autorEncontradoEnBD.isPresent()){
                Autor autorExistente = autorEncontradoEnBD.get();
                libro.getAutores().add(autorExistente);
                autorExistente.getLibros().add(libro);
                autorRepository.save(autorExistente);
            }else{
                Autor autorNuevo = new Autor(datosAutor);
                libro.getAutores().add(autorNuevo);
                autorNuevo.getLibros().add(libro);
                autorRepository.save(autorNuevo);
            }
            libroRepository.save(libro);
            System.out.println(libro);
            System.out.println("Libro encontrado y guardado exitosamente");
        }
    }
    private void listarLibrosRegistrados(){
        List<Libro> listaLibros = libroRepository.findAll();
        if (listaLibros.isEmpty()){
            System.out.println("No hay libros registrados");
            return;
        }
        listaLibros.forEach(System.out::println);
    }
    private void listarAutoresRegistrados(){
        List<Autor> listaAutores = autorRepository.findAll();
        if (listaAutores.isEmpty()){
            System.out.println("No hay autores registrados");
            return;
        }
        listaAutores.forEach(System.out::println);
    }
    private void listarAutoresVivosPorAno(){
        System.out.println("Escribe el año en el que deseas buscar autores vivos:");
        try {
            int ano = teclado.nextInt();
            List<Autor> listaAutores = autorRepository.listarAutoresVivosPorAno(ano);
            if(listaAutores.isEmpty()){
                System.out.println("No hay autores registrados vivos en ese año");
                return;
            }
            listaAutores.forEach(System.out::println);
        }catch (Exception e){
            System.out.println("No es posible completar la operación");
        }
    }
    private void listarLibrosPorIdioma(){
        System.out.println("""
                Ingresa el código del idioma para buscar libros:
                es - español
                en - inglés 
                fr - fránces
                pt - portugués
                """);
        try {
            String idioma = teclado.nextLine();
            List<Libro> listaLibros = libroRepository.listarLibrosPorIdioma(idioma);
            if (listaLibros.isEmpty()){
                System.out.println("No hay libros registrados con ese idioma");
                return;
            }
            listaLibros.forEach(System.out::println);
        }catch (Exception e){
            System.out.println("No es posible completar la operación");
        }
    }

}
