package ni.edu.uam.proyectolaboratorio.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import ni.edu.uam.proyectolaboratorio.database.JPAUtil;
import ni.edu.uam.proyectolaboratorio.model.Equipo;
import ni.edu.uam.proyectolaboratorio.model.TipoEquipo;

import java.util.List;

public class EquipoDAO {
    public void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(equipo);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void actualizar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(equipo);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean eliminar(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipo = em.find(Equipo.class, id);
            if (equipo == null) {
                tx.rollback();
                return false;
            }
            em.remove(equipo);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Equipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo ORDER BY e.id",
                    Equipo.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Equipo> buscar(String nombre, TipoEquipo tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo WHERE 1 = 1";
            if (nombre != null && !nombre.isBlank()) {
                jpql += " AND LOWER(e.nombre) LIKE :nombre";
            }
            if (tipo != null) {
                jpql += " AND e.tipoEquipo.id = :idTipo";
            }
            jpql += " ORDER BY e.id";

            TypedQuery<Equipo> consulta = em.createQuery(jpql, Equipo.class);
            if (nombre != null && !nombre.isBlank()) {
                consulta.setParameter("nombre", "%" + nombre.trim().toLowerCase() + "%");
            }
            if (tipo != null) {
                consulta.setParameter("idTipo", tipo.getId());
            }
            return consulta.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existePorCodigo(String codigo) {
        return existePorCodigo(codigo, null);
    }

    public boolean existePorCodigo(String codigo, Long idExcluir) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM Equipo e WHERE e.codigo = :codigo";
            if (idExcluir != null) {
                jpql += " AND e.id <> :idExcluir";
            }
            TypedQuery<Long> consulta = em.createQuery(jpql, Long.class);
            consulta.setParameter("codigo", codigo);
            if (idExcluir != null) {
                consulta.setParameter("idExcluir", idExcluir);
            }
            return consulta.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public void demostrarRollback(String codigoExistente, TipoEquipo tipoEquipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo primero = new Equipo();
            primero.setCodigo("PRUEBA-" + System.currentTimeMillis());
            primero.setNombre("Equipo temporal");
            primero.setEstado("Disponible");
            primero.setCantidad(1);
            primero.setTipoEquipo(tipoEquipo);
            em.persist(primero);
            em.flush();

            Equipo duplicado = new Equipo();
            duplicado.setCodigo(codigoExistente);
            duplicado.setNombre("Equipo duplicado");
            duplicado.setEstado("Disponible");
            duplicado.setCantidad(1);
            duplicado.setTipoEquipo(tipoEquipo);
            em.persist(duplicado);
            em.flush();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
