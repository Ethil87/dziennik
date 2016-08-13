/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vxm.michta;
//import java.sql.Date;
import java.util.Date;
/**
 *
 * @author amichta
 */
public class SetGetClass {
    private int id;
    private String przedmiot;
    private String prowadzacy;
    private double przewidywana;
    private double ostateczna;
    private Date data;
    private int semestr;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getPrzedmiot(){
        return przedmiot;
    }
    
    public void setPrzedmiot(String przedmiot){
        this.przedmiot = przedmiot;
    }
    
    public String getProwadzacy(){
        return prowadzacy;
    }
    
    public void setProwadzacy(String prowadzacy){
        this.prowadzacy = prowadzacy;
    }
    
    public double getPrzewidywana(){
        return przewidywana;
    }
    
    public void setPrzewidywana(double przewidywana){
        this.przewidywana = przewidywana;
    }
    
    public double getOstateczna(){
        return ostateczna;
    }
    
    public void setOstateczna(double ostateczna){
        this.ostateczna = ostateczna;
    }
    
    public Date getData(){
        return data;
    }
    
    public void setData(Date data){
        this.data = data;
    }
    
    public int getSemestr(){
        return semestr;
    }
    
    public void setSemestr(int semestr){
        this.semestr = semestr;
    }
    
}
