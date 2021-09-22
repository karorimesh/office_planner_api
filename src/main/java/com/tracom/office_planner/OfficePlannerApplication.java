package com.tracom.office_planner;

import com.tracom.office_planner.Employee.Employee;
import com.tracom.office_planner.Organization.Organization;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OfficePlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficePlannerApplication.class, args);

////        System.out.println("Office Planner");
//        Organization tracom = new Organization();
//        tracom.setOrganization_id(1);
//        tracom.setOrganization_name("Tracom");
//
//        Configuration cfg = new Configuration().configure()
//                .addAnnotatedClass(Organization.class)
//                .addAnnotatedClass(Employee.class)
//                .addAnnotatedClass(User.class);
////        ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
//        SessionFactory sf = cfg.buildSessionFactory();
//        Session s = sf.openSession();
//        s.beginTransaction();
//        s.save(tracom);
//
//        s.getTransaction().commit();
//        s.close();

    }


}
