/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vxm.michta;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author amichta
 */
public class PolaczenieHibernate {
    
    private static final SessionFactory sessionFactory = buildSessionFactory();
    
    private static SessionFactory buildSessionFactory(){
        try{
            Configuration configuration = new Configuration();
            configuration.configure();
            StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
            ssrb.applySettings(configuration.getProperties());
            ServiceRegistry sr = ssrb.build();
            return configuration.buildSessionFactory(sr);
        }catch(Exception e){
            throw new ExceptionInInitializerError(e);
        }
    }
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }
}
