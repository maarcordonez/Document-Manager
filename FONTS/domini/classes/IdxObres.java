package domini.classes;

import domini.excepcions.ExceptionAutorNoExisteix;
import domini.excepcions.ExceptionObraJaExisteix;
import domini.excepcions.ExceptionObraNoExisteix;
import domini.utils.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Estructura on s'emmagatzemen els documents en funcio de l'autor i el títol. La idea és que més endavant només contingui
 * un path amb l'adreça de memoria on es troba el document. Com ara no hi ha persistencia es guarda el document en comptes del path
 * @author Marc Ordonez
 */
public class IdxObres {
    // TreeMap on cada node es un autor, dins de cada autor es guardarà un map amb els títols de les seves obres i el document
    // associat a aquell títol i autor.
    private TreeMap<String, TreeMap<String, LocalDateTime>> map = new TreeMap<String, TreeMap<String, LocalDateTime>>();
    // Número d'obres que hi ha a l'estructura.
    private int numObres = 0;

    /**
     * S'afegeix l'obra d'un autor amb un titol en cas de que l'autor no tingui cap obra amb aquest titol.
     * Si l'autor encara no existia, es crea un nou autor amb cap titol associat.
     * @param autor Autor de l'obra.
     * @param obra Titol de l'obra.
     * @throws ExceptionObraJaExisteix Es llença si l'autor ja te un titol amb el mateix nom.
     */
    public void afegirObra(String obra, String autor) throws ExceptionObraJaExisteix {
        //SortedSet<String>  llistaObres = map.get(autor);
        //llistaObres.add(obra);
        //map.put(autor, llistaObres);
        if(obra != null && autor != null) {
            if (!map.containsKey(autor)) afegirAutor(autor);
            if (map.get(autor).containsKey(obra)) throw new ExceptionObraJaExisteix(autor, obra);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            map.get(autor).put(obra, now);
            ++numObres;
        }
    }

    /**
     * Es crea un nou autor amb cap document associat. Només es crida aquesta funcio des de afegirObra i nomes en el cas
     * en que l'autor no existeixi. Per tant, mai es matxacara la informacio de l'autor perque no existia.
     * @param autor Autor que es vol afegir.
     */
    public void afegirAutor(String autor) {
        if (autor != null) {
            TreeMap<String, LocalDateTime> buit = new TreeMap<>();
            map.put(autor, buit);
        }
    }

    /**
     * Retorna les obres d'un autor ordenades alfabeticament.
     * @param autor Autor del que volem fer la consulta.
     * @return Retorna un ArrayList de String amb els titols de les obres de l'autor ordenats alfabeticament.
     * Si l'autor no te cap obra retorna una llista buida.
     * @throws ExceptionAutorNoExisteix Llença l'excepcio si no hi ha cap autor amb aquest nom.
     */
    public ArrayList<String> consultaObresAutor(String autor) throws ExceptionAutorNoExisteix {
        if (autor != null) {
            if (!map.containsKey(autor)) throw new ExceptionAutorNoExisteix(autor);
            TreeMap<String, LocalDateTime> llistaObres = map.get(autor);
            ArrayList<String> output = new ArrayList<>();
            for (Map.Entry<String, LocalDateTime> entry : llistaObres.entrySet()) {
                output.add(entry.getKey());
            }
            return output;
        }
        return new ArrayList<>();
    }


    /**
     * S'elimina a un autor. Aquesta funcio nomes es crida des de eliminarObraAutor. Per tant, ens assegurem de fer-la
     * servir unicament quan l'autor ja no tingui obres.
     * @param autor Nom de l'autor que volem eliminar.
     */
    private void eliminarAutor(String autor){
        if(autor != null) {
            map.remove(autor);
        }
    }

    /**
     * Elimina l’obra d’un autor
     * @param obra Nom de l'obra a eliminar
     * @param autor Autor de l'obra
     * @return Retorna true si aquell autor ja no té cap obra. En aquest cas també s’eliminarà a l’autor.
     * @throws ExceptionAutorNoExisteix Llença l'excepció si l'autor no existeix
     * @throws ExceptionObraNoExisteix Llença l'excepció si l'autor no té cap obra amb aquell nom
     */
    public boolean eliminarObraAutor(String obra, String autor) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix {
        if (autor != null && obra != null) {
            if (!map.containsKey(autor)) throw new ExceptionAutorNoExisteix(autor);
            if (!map.get(autor).containsKey(obra)) throw new ExceptionObraNoExisteix(autor, obra);
            map.get(autor).remove(obra);
            --numObres;
            if (map.get(autor).isEmpty()) {
                eliminarAutor(autor);
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna un ArrayList de Pairs amb totes les obres d’un autor. El primer element del pair es el titol i el segon l’autor.
     * @return ArrayList de pairs titol, autor
     */
    public ArrayList<Pair<String, String>> getTitolsAutors() {
        ArrayList<Pair<String, String>> output = new ArrayList<>();
        for (Map.Entry<String, TreeMap<String, LocalDateTime>> entry : map.entrySet()) {
            for (Map.Entry<String, LocalDateTime> entry2: entry.getValue().entrySet()) {
                Pair p = new Pair<>(entry2.getKey(), entry.getKey());
                output.add(p);
            }
        }
        return output;
    }

    /**
     * Modifica un document. Donat un autor i un titol busca el document original. Si l’autor i el titol nous son els mateixos als actuals, nomes modifica el contingut
     * del document sobreescrivint amb el nou document. En cas de que canvii l’autor o el titol, primer s’elimina el document amb l’autor i titols originals i despres
     * s’afegeix el nou document amb el titol i l’autor nous.
     * @param autor Autor antic del document  que s'ha modificat.
     * @param titol Títol antic del document que s'ha modificat.
     * @param nouAutor Nou nom de l'autor (pot ser el mateix que l'antic)
     * @param nouTitol Nou títol de l'obra (pot ser el mateix que l'antic)
     * @return Si a l’eliminar el document antic l’autor antic es queda sense obres, s’eliminara a l’autor antic i es retornara true. En cas contrari es retorna false.
     * @throws ExceptionAutorNoExisteix Llença l'excepció quan no existeix l'autor antic o el nou
     * @throws ExceptionObraNoExisteix Llença l'excepció quan no existeix el títol antic
     * @throws ExceptionObraJaExisteix Llença l'excecpció si la nova obra té un títol d'una obra que ja té l'autor nou
     */
    public boolean modificaDocument(String autor, String titol, String nouAutor, String nouTitol) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix, ExceptionObraJaExisteix {
        if (titol != null && autor != null && nouAutor != null && nouTitol != null) {
            boolean empty = false;
            empty = eliminarObraAutor(titol, autor);
            afegirObra(nouTitol, nouAutor);
            if (!nouAutor.equals(autor)) return empty;
            else return false;
        }
        return false;
    }

    /**
     * @return Retorna el numero d'autors registrats
     */
    public int getNumAutors()  {
        return map.size();
    }

    /**
     * @return Retorna el numero d'obres registrades
     */
    public int getNumObres() {
        return numObres;
    }

    /**
     * @return Retorna l'estructura d'index obres
     */
    public TreeMap<String, TreeMap<String, LocalDateTime>> getIdxObres() {
        return map;
    }

    /** Inicialitza index obres
     * @param idxO Index d'obres
     */
    public void inicialitzarIdxObres(TreeMap<String, TreeMap<String, LocalDateTime>> idxO) {
        map =  idxO;
        numObres = idxO.size();
    }

    /** Comprova si existeix un document identificat per titol i autor
     * @param titol Titol del document
     * @param autor Autor del document
     * @return Cert si existeix i Fals si no
     */
    public boolean existeixDocument(String titol, String autor) {
        if (titol != null && autor != null) {
            if (map.containsKey(autor)) return map.get(autor).containsKey(titol);
        }
        return false;
    }

    /** 
     * @return Retorna ArrayList de títol, autor i data de totes les obres de l'index
     */
    public ArrayList<ArrayList<String>> getTitolsAutorsData() {
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        for (Map.Entry<String, TreeMap<String, LocalDateTime>> entry : map.entrySet()) {
            for (Map.Entry<String, LocalDateTime> entry2: entry.getValue().entrySet()) {
                ArrayList p = new ArrayList<String>();
                p.add(entry2.getKey());
                p.add(entry.getKey());
                String data = String.valueOf(entry2.getValue());
                data = data.substring(0, 16);
                data = data.replace("T", " ");
                p.add(data);
                output.add(p);
            }
        }
        return output;
    }

    /** Retorna la data de l'obra identificada per titol autor
     * @param titol Titol del document
     * @param autor Autor del document
     * @return La data de l'obra identificada
     */
    public String getData(String titol, String autor) {
        if (titol != null && autor != null) {
            if (map.containsKey(autor) && map.get(autor).containsKey(titol)) {
                String data = String.valueOf(map.get(autor).get(titol));
                data = data.substring(0, 16);
                data = data.replace("T", " ");
                return data;
            }
        }
        return "XXXX-XX-XX XX:XX"; // si es null retorna aixo pero mai s'hauria d'arribar a aquest cas
    }
}