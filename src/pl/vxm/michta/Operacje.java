/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vxm.michta;

import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author amichta
 */
public class Operacje {
    
    public List<SetGetClass> zaczytajTabele(String q){
       
        //usunTabele();
        Session session = PolaczenieHibernate.getSessionFactory().getCurrentSession();
        List<SetGetClass> sgc = null;
        try{
        session.beginTransaction();
            try{
                sgc = session.createQuery(q).list();
                session.getTransaction().commit();
            }catch(Exception e){
                session.getTransaction().rollback();
                PolaczenieHibernate.getSessionFactory().close();
            }
        }catch(Exception e){
            ;
        }
        return sgc;
    }
    
    public void zapiszTabele(SetGetClass sgc){
        Session session = PolaczenieHibernate.getSessionFactory().getCurrentSession();
        try{
            session.beginTransaction();
            try{
                session.saveOrUpdate(sgc);
                session.getTransaction().commit();
                session.close();
            }catch(Exception e){
            session.getTransaction().rollback();
            PolaczenieHibernate.getSessionFactory().close();
            }
        }catch(Exception e){
            ;
        }
    }
    
    public void usunZtabeli(SetGetClass sgc){
        Session session = PolaczenieHibernate.getSessionFactory().getCurrentSession();
        try{
            session.beginTransaction();
            try{
                session.delete(sgc);
                session.getTransaction().commit();
                session.close();
            }catch(Exception e){
            session.getTransaction().rollback();
            PolaczenieHibernate.getSessionFactory().close();
            }
        }catch(Exception e){
            ;
        }
    }
}
