package cj.poc.db;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class DBManagerHelper {

    private static Properties mainProperties = new Properties();
    private static EntityManagerFactory emf;
    private static ThreadLocal<EntityManager> threadLocal;

    static {
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("people", mainProperties);
            threadLocal = new ThreadLocal<EntityManager>();
        }
        EntityManager em = threadLocal.get();

        if (em == null) {
            em = emf.createEntityManager();
            // set your flush mode here
            threadLocal.set(em);
        }
        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
            em.close();
            threadLocal.set(null);
        }
    }

    public static void closeEntityManagerFactory() {
        emf.close();
    }

    public static void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    public static void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    public static void commit() {
        getEntityManager().getTransaction().commit();
    }


    public static Properties loadProp(String propName) {

        String versionString = null;

        //to load application's properties, we use this class

        FileInputStream file;
        String path = "./" + propName;
        log.debug("load: " + path);
        //the base folder is ./, the root of the main.properties file
        try {
            file = new FileInputStream(path);
            mainProperties.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainProperties;
    }
}
