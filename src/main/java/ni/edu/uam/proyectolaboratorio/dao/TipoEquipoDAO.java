package ni.edu.uam.proyectolaboratorio.dao;

import jakarta.persistence.EntityManager;
import ni.edu.uam.proyectolaboratorio.database.JPAUtil;
import ni.edu.uam.proyectolaboratorio.model.TipoEquipo;

import java.util.List;

public class TipoEquipoDAO {
    public List<TipoEquipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM TipoEquipo t ORDER BY t.nombre", TipoEquipo.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
