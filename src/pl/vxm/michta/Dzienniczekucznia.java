/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vxm.michta;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author amichta
 */
public class Dzienniczekucznia extends JFrame implements ActionListener, MouseListener{

    private Date currentTime; 
    private JLabel zegar;
    private JTable tab, tabsrednich;
    private JButton bOdswiez, bZapisz, bUsun, bWyczysc;
    
    private JLabel[] labele = new JLabel[7];
    private String nazwylabeli[] = {"ID:", "Przedmiot:", "Prowadzący:", "Przewidywana ocena:", "Ostateczna ocena:", "Data modyfikacji:", "Semestr:"};
    private int labelSize[] = {10,450,150,20};
    
    private JTextField[] fieldy = new JTextField[7];
    private int fieldsize[] = {150,450,300,20};
    
    private String[] columnNames = {"ID", "Przedmiot", "Prowadzący", "Przewidywana ocena", "Ostateczna ocena", "Data ostatniej zmiany", "Semestr"};
    private Object[][] data;
    private String q="from SetGetClass";
    
    private String[] columnNames2 = {"Semestr", "Średnia"};
    
    
    public Dzienniczekucznia(){
        //ogolne wlasciwosci glownego okna
        setSize(1280,720);
        setTitle("Dziennik");
        setLayout(null);
        setLocationRelativeTo(null);
        
        //zegarek
        zegar = new JLabel();
        zegar.setBounds(10,10,150,10);
        zegarek();
        add(zegar);
        
        //JPanel z tabela i przyciskiem sciagajacym dane
        tab = new JTable(){
        @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tab.getTableHeader().setReorderingAllowed(false);
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        tab.setModel(model);
        tab.setPreferredScrollableViewportSize(new Dimension(1180,300));
        tab.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tab);
        JPanel pan = new JPanel();
        pan.setBorder(BorderFactory.createLineBorder(Color.black));
        pan.setBounds(10,50,1240,370);
        add(pan);
        pan.add(scrollPane);
        bOdswiez = new JButton("Odswiez");
        bOdswiez.addActionListener(this);
        pan.add(bOdswiez);
        zaczytajTabele("from SetGetClass");
        tab.addMouseListener(this);
        tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //tabela srednich ocen
        tabsrednich = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabsrednich.getTableHeader().setReorderingAllowed(false);
        DefaultTableModel model2 = new DefaultTableModel(data, columnNames2);
        tabsrednich.setModel(model2);
        tabsrednich.setPreferredScrollableViewportSize(new Dimension(190,120));
        tabsrednich.setFillsViewportHeight(true);
        JScrollPane scrollPane3 = new JScrollPane(tabsrednich);
        JPanel pan3 = new JPanel();
        pan3.setBorder(BorderFactory.createLineBorder(Color.black));
        pan3.setBounds(870,450,200,150);
        add(pan3);
        pan3.add(scrollPane3);
        sredniaTabela();
        
        
        
        //chart panel
        JPanel pan2 = new JPanel();
        pan2.setBorder(BorderFactory.createLineBorder(Color.black));
        pan2.setBounds(460,450,400,170);
        add(pan2);
        pan2.add(wykres());

        
        //interfejs dodajacy pola dla uzytkownika
        int y=0;
        for(int i=0; i < labele.length; i++){
            String temp = nazwylabeli[i];
            JLabel lbl = new JLabel(nazwylabeli[i]);
            labele[i] = lbl;
            labele[i].setText(temp);
            labele[i].setBounds(labelSize[0],labelSize[1]+y,labelSize[2],labelSize[3]);
            y = y+20;
            add(labele[i]);
        }
        
        y=0;
        for(int i = 0; i < fieldy.length; i++){
            JTextField jtf = new JTextField();
            fieldy[i] = jtf;
            fieldy[i].setBounds(fieldsize[0], fieldsize[1]+y, fieldsize[2], fieldsize[3]);
            y = y+20;
            add(fieldy[i]);
        }
        fieldy[0].setEditable(false);
        fieldy[5].setEditable(false);
        
        
        //buttony dla użytkownika
        bZapisz = new JButton("Zapisz");
        bZapisz.setBounds(150,600,100,20);
        bZapisz.addActionListener(this);
        add(bZapisz);
        
        bUsun = new JButton("Usuń");
        bUsun.setBounds(250,600,100,20);
        bUsun.addActionListener(this);
        add(bUsun);
        
        bWyczysc = new JButton("Wyczyść");
        bWyczysc.setBounds(350,600,100,20);
        bWyczysc.addActionListener(this);
        add(bWyczysc);
    }
    
    
    //Rysuje wykres sredniej ocen
    public ChartPanel wykres(){
        List sr = srednia();
        DefaultCategoryDataset danechart = new DefaultCategoryDataset();
        int sredniasize = srednia().size();
        for(int i=1; i <= sredniasize ; i++){
            danechart.addValue(srednia().get(i-1), "1", (Comparable) i);
        }
        
        JFreeChart chart = ChartFactory.createBarChart("Średnia", "", "", danechart, PlotOrientation.VERTICAL, false, false, false);
        CategoryPlot catPlot = chart.getCategoryPlot();
        catPlot.setRangeGridlinePaint(Color.BLACK);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(395,163));
        return chartPanel;
    
    }
    
    //wyswietla w interwale 1000ms czas
    public void zegarek(){
        currentTime = new Date();
        String dataZh = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(currentTime);
        zegar.setText(dataZh);
        Timer timer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                currentTime = new Date();
                String dataZh = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(currentTime);
                zegar.setText(dataZh);
            }
        });
        timer.start();
    }
    
    //Zaczytuje dane z bazy
    private void zaczytajTabele(String q){
        wyczyscTabele();
        DefaultTableModel model = (DefaultTableModel) tab.getModel();
        Operacje o = new Operacje();
        List<SetGetClass> sgc = o.zaczytajTabele(q);
        Iterator iter = sgc.iterator();
        while(iter.hasNext()) {
            SetGetClass s = (SetGetClass) iter.next();
            model.addRow(new Object[]{s.getId(), s.getPrzedmiot(), s.getProwadzacy(), s.getPrzewidywana(), s.getOstateczna(), s.getData(), s.getSemestr()});
        }
    }
    
    //czyści JTable 
    private void wyczyscTabele(){
        DefaultTableModel model = (DefaultTableModel) tab.getModel();
        while (model.getRowCount() > 0) {
            for (int i = 0; i < model.getRowCount(); i++) {
                model.removeRow(i);
            }
        }
    }
    
    //Listiner dla buttonow
    @Override
    public void actionPerformed(ActionEvent e){
        Object zrodlo = e.getSource();
        if (zrodlo == bOdswiez){
            zaczytajTabele("from SetGetClass");
        }else if (zrodlo == bWyczysc){
            wyczyscFieldy();  
        }else if (zrodlo == bZapisz){
            Object[] opcja = {"TAK", "NIE"};
            int dialog = JOptionPane.showOptionDialog(null,"Czy na pewno chcesz zapisać?", "UWAGA!", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION,null,opcja,opcja[1]);
            if (dialog == 0){
                try {
                zapiszDane();
                } catch (ParseException ex) {
                Logger.getLogger(Dzienniczekucznia.class.getName()).log(Level.SEVERE, null, ex);
                }
                zaczytajTabele("from SetGetClass");
            }
        }else if (zrodlo == bUsun){
            int id = Integer.parseInt(fieldy[0].getText());
            SetGetClass sgt = new SetGetClass();
            sgt.setId(id);
            Operacje o = new Operacje();
            o.usunZtabeli(sgt);
            zaczytajTabele("from SetGetClass");
            wyczyscFieldy();
        }
    }
    
    //
    @Override
    public void mousePressed(MouseEvent e){
        wybierzZtabeli();
        
    }
    
    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseReleased(MouseEvent e) {

    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }
    
    //zapisuje dane podane przez uzytkownika do bazy danych
    public void zapiszDane() throws ParseException{

        SetGetClass sgc = new SetGetClass();
        if ("".equals(fieldy[0].getText())){
            boolean blad=false;
            for(int i = 0; i < fieldy.length; i++){
                try{
                switch(i){
                    //case 0: sgc.setId(Integer.parseInt(fieldy[i].getText()));
                    //break;
                    case 1: sgc.setPrzedmiot(fieldy[i].getText());
                    break;
                    case 2: sgc.setProwadzacy(fieldy[i].getText());
                    break;
                    case 3: sgc.setPrzewidywana(Double.parseDouble(fieldy[i].getText()));
                    break;
                    case 4: sgc.setOstateczna(Double.parseDouble(fieldy[i].getText()));
                    break;
                    case 5: sgc.setData(currentTime);
                    break;
                    case 6: sgc.setSemestr(Integer.parseInt(fieldy[i].getText()));
                    break;
                }
                }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "Jedna lub więcej z informacji podanych przez Ciebie jest NIEPRAWIDŁOWA!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
                        blad = true;
                        break;
                };
            }
            if(blad == false){
                wyczyscFieldy();
                Operacje o = new Operacje();
                o.zapiszTabele(sgc);
            }
        }else {
            boolean blad=false;
            for(int i = 0; i < fieldy.length; i++){
                try{
                    switch(i){
                        case 0: sgc.setId(Integer.parseInt(fieldy[i].getText()));
                        break;
                        case 1: sgc.setPrzedmiot(fieldy[i].getText());
                        break;
                        case 2: sgc.setProwadzacy(fieldy[i].getText());
                        break;
                        case 3: sgc.setPrzewidywana(Double.parseDouble(fieldy[i].getText()));
                        break;
                        case 4: sgc.setOstateczna(Double.parseDouble(fieldy[i].getText()));
                        break;
                        case 5: sgc.setData(currentTime);
                        break;
                        case 6: sgc.setSemestr(Integer.parseInt(fieldy[i].getText()));
                        break;
                    }
                }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "Jedna lub więcej z informacji podanych przez Ciebie jeste NIEPRAWIDŁOWA!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
                        blad = true;
                        break;
                };
            }
            if(blad == false){
                wyczyscFieldy();
                Operacje o = new Operacje();
                o.zapiszTabele(sgc);
            }
        }
    }  
    
    //czysci pola wypelnione przez uzytkownika
    public void wyczyscFieldy(){
        for(int i = 0; i < fieldy.length; i++){
            fieldy[i].setText("");
        }
    }
    
    //przepisuje dane z tabeli do pól uzytkownika
    public void wybierzZtabeli(){
        try {
            int row = tab.getSelectedRow();
            for (int i=0; i<7; i++){
                String klik2 = (tab.getModel().getValueAt(row, i).toString());
                switch(i){
                    case 0: fieldy[i].setText(klik2);
                    break;
                    case 1: fieldy[i].setText(klik2);
                    break;
                    case 2: fieldy[i].setText(klik2);
                    break;
                    case 3: fieldy[i].setText(klik2);
                    break;
                    case 4: fieldy[i].setText(klik2);
                    break;
                    case 5: fieldy[i].setText(klik2);
                    break;
                    case 6: fieldy[i].setText(klik2);
                    break;
                }
            }
            
        }catch(Exception e){
                    
        }
    }
    
    
    //Oblicza srednia w semestrach
    public List<Double> srednia(){
        
        
        //Sprawdza ile semestrow jest w bazie
        List<Double> sredniaocen = new ArrayList<>();
        double ocena=0;
        List<Integer> listasemestrow = new ArrayList<>();
        int sem = (int) tab.getModel().getValueAt(0, 6);
        listasemestrow.add(sem);
        for (int i=1; i<tab.getRowCount(); i++){
            int temp = (int)tab.getModel().getValueAt(i,6);
            if (sem != temp){
                listasemestrow.add(temp);
                sem = temp;
            }
        }
        
        //liczy srednio w poszczegolnych semestrach
        Iterator it = listasemestrow.iterator();
        while (it.hasNext()) {
            double powtorzenia = 0;
            int ite = (int) it.next();
            for (int i = 0; i < tab.getRowCount(); i++) {
                int war = (int) tab.getModel().getValueAt(i, 6);
                if (ite == war) {
                    ocena = ocena + (double) tab.getModel().getValueAt(i, 4);
                    powtorzenia++;
                }
            }
            double sr = ocena / powtorzenia;
            ocena = 0;
            sredniaocen.add(sr);

        }
        return sredniaocen;
    }
    
    //wystwietla srednio w JTable
    public void sredniaTabela(){
        DefaultTableModel model2 = (DefaultTableModel) tabsrednich.getModel();
        for (int i = 0; i<srednia().size(); i++){
            model2.addRow(new Object[]{i+1, srednia().get(i)});
        }
        
        
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Dzienniczekucznia d = new Dzienniczekucznia();
                d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                d.setVisible(true);
    }
    
}
