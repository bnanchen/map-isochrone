package ch.epfl.isochrone.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.tiledmap.CachedTileProvider;
import ch.epfl.isochrone.tiledmap.IsochroneTileProvider;
import ch.epfl.isochrone.tiledmap.OSMTileProvider;
import ch.epfl.isochrone.tiledmap.TileProvider;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.Date.Month;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Service;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

public final class IsochroneTL {
    private static final String OSM_TILE_URL = "http://b.tile.openstreetmap.org/";
    private static final int INITIAL_ZOOM = 11;
    private static final PointWGS84 INITIAL_POSITION = new PointWGS84(Math.toRadians(6.476), Math.toRadians(46.613));
    private static final String INITIAL_STARTING_STOP_NAME = "Lausanne-Flon";
    private static final int INITIAL_DEPARTURE_TIME = SecondsPastMidnight.fromHMS(6, 8, 0);
    private static final Date INITIAL_DATE = new Date(1, Month.OCTOBER, 2013);
    private static final int WALKING_TIME = 5 * 60;
    private static final double WALKING_SPEED = 1.25;
    private TimeTableReader reader; 
    private Graph graph;

    private static TiledMapComponent tiledMapComponent; // ajouté par le prof!!
    private Date date = INITIAL_DATE;
    private int departureTime = INITIAL_DEPARTURE_TIME;
    private Stop startingStop = null;

    public IsochroneTL() throws IOException {
        TileProvider bgTileProvider = new CachedTileProvider(new OSMTileProvider(OSM_TILE_URL));
        tiledMapComponent = new TiledMapComponent(INITIAL_ZOOM);

        //===========================================================================================
        
        
        try {
            reader = new TimeTableReader("/time-table/");
        
           // le reader nous permet d'obtenir un Graph et un timeTable
           TimeTable timetable = reader.readTimeTable();
           graph = reader.readGraphForServices(timetable.stops(), timetable.servicesForDate(INITIAL_DATE), WALKING_TIME, WALKING_SPEED);
           Stop departureStop = new Stop("void", new PointWGS84(0.0, 0.0));


           Object[] stops = timetable.stops().toArray();
           

           for(int i = 0; i < timetable.stops().size(); i++){

               //on fait la boucle jusqu'a ce que le stop aie le même nom que le nom passé en argument puis on sort immédiatement.
               if(stops[i].hashCode() == new String(INITIAL_STARTING_STOP_NAME).hashCode()){
                   departureStop = (Stop) stops[i];
                   this.startingStop = departureStop;
                   break;
               }
           }
           
           FastestPathTree tree = graph.fastestPaths(departureStop, INITIAL_DEPARTURE_TIME);

           IsochroneTileProvider tileIsochroneProvider = new IsochroneTileProvider(tree, getColor(), WALKING_SPEED);
           List<TileProvider> listProvider = new ArrayList<TileProvider>();
           
           listProvider.add(bgTileProvider);
           listProvider.add(tileIsochroneProvider);
           
           
           tiledMapComponent.setProviderList(listProvider);
           tiledMapComponent.setIsochronePosition(INITIAL_POSITION.toOSM(INITIAL_ZOOM).roundedX(),INITIAL_POSITION.toOSM(INITIAL_ZOOM).roundedY());
          
           
           
        }catch (IOException e) {
            // TODO Auto-generated catch block
            
        }
        
        //===========================================================================================
    }

    public void setDate(Date date){
        Date oldest = this.date;
        this.date = new Date(date.day(), date.intMonth(), date.year());
        updateServices(oldest);
        this.date = date;
        
    }
    
    public static List<Color> getColor(){
        List<Color> color = new ArrayList<Color>();
        color.add(Color.black);
        color.add(new Color(0, 0, 128));//mélange bleu noir.
        color.add(Color.blue);
        color.add(new Color(0, 128, 255));//mélange bleu vert
        color.add(Color.green);
        color.add(new Color(128, 255, 0));//mélange vert jaune
        color.add(Color.yellow);
        color.add(new Color(255, 128, 0));//mélange jaune rouge
        color.add(Color.red);
        return color;
    }
    
    public void setDepartureTime(int departureTime){
        this.departureTime = departureTime;
        updateFastestPathTree(graph);
    }
    
    public void setStartingStop(Stop startingStop){
        this.startingStop = startingStop;
        updateFastestPathTree(graph);
    }
    
    public void updateServices(Date lastDate){
        System.out.println(lastDate);
        TimeTable timetable = reader.readTimeTable();
        Set<Service> servicesOfLastDate = timetable.servicesForDate(lastDate);
        Set<Service> servicesOfPresentDate = timetable.servicesForDate(this.date);
        System.out.println("lastDate: "+ lastDate +" date: "+ this.date);
        boolean bool = true;
        for(Service e: servicesOfLastDate){
            if(!(e.isOperatingOn(this.date))){
                System.out.println("PUUUUUUTE!");
                bool = false;
                break;
            }
        }
        System.out.println(bool);
        if(bool){
            for(Service e: servicesOfPresentDate){
                if(!(e.isOperatingOn(lastDate))){
                    bool = false;
                    break;
                }
                
            }
        }
        System.out.println(bool);
        if(!bool){
            System.out.println("passage update");
            updateGraph(timetable);
        }
        else{
            System.out.println("passage non update");
        }
    }
    
    public void updateGraph(TimeTable timetable){
        System.out.println("COMME DES CONNARDS!");
        graph = reader.readGraphForServices(timetable.stops(), timetable.servicesForDate(this.date), WALKING_TIME, WALKING_SPEED);
        updateFastestPathTree(graph);
    }
    
    public void updateFastestPathTree(Graph graph){
        System.out.println("COMME DES PUUUUUUTES!");
        FastestPathTree tree = graph.fastestPaths(startingStop, departureTime);
        updateIsochroneMap(tree);
    }
    
    public static void updateIsochroneMap(FastestPathTree tree){
       
        IsochroneTileProvider provider = new IsochroneTileProvider(tree, getColor(), WALKING_SPEED);
        TileProvider bgTileProvider = new CachedTileProvider(new OSMTileProvider(OSM_TILE_URL));
        List<TileProvider> listProvider = new ArrayList<TileProvider>();
        listProvider.add(bgTileProvider);
        listProvider.add(provider);
        tiledMapComponent.setProviderList(listProvider);
    }
    
    private JPanel createFrontPanel(){
        JPanel frontPanel = new JPanel(new FlowLayout());
        JLabel departureLabel = new JLabel("Départ");
        JLabel dateLabel = new JLabel("Date et heure");
        JSeparator separator = new JSeparator();
        
        
        
        Combo combo = new Combo();
        Spinner spinner = new Spinner();//ChangeListener
        
        frontPanel.add(departureLabel);
        frontPanel.add(combo);
        frontPanel.add(separator);
        frontPanel.add(dateLabel);
        frontPanel.add(spinner);
        frontPanel.setBackground(new Color(203, 203, 203));
        return frontPanel;
    }
    public class Spinner extends JSpinner implements ChangeListener{

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public Spinner(){
            super(new SpinnerDateModel());
            this.addChangeListener(this);
        }
        @Override
        public void stateChanged(ChangeEvent arg0) {
            // TODO Auto-generated method stub
            java.util.Date dateJava = (java.util.Date)this.getValue();
            Date newDate = new Date(dateJava);
           
            //attention à la condition, il faut voir un peu tout autour
            if(!(date.equals(newDate))){
                System.out.println("changement de date");
            setDate(newDate);
            }
            else{
                System.out.println("changement d'heure");
            setDepartureTime(SecondsPastMidnight.fromHMS(dateJava.getHours(), dateJava.getMinutes(), dateJava.getSeconds()));
            }
           
            
        }
        
    }
    
    public class Combo extends JComboBox<String> implements ActionListener{
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public Combo(){
            
            
            TimeTable timetable = reader.readTimeTable();
            
            Object[] table = timetable.stops().toArray();
            String[] tab = new String[table.length];
            for(int i =0; i< tab.length; i++){
                tab[i]= table[i].toString();
            }
            
            Arrays.sort(tab);
            
            
            for(int i = 0; i < tab.length; i++){
                this.addItem((String)tab[i]);
            }
            this.setSelectedItem(INITIAL_STARTING_STOP_NAME);
            

        this.addActionListener(this);
        }
        
        
        public void actionPerformed(ActionEvent e){
            String select = (String) this.getSelectedItem();
            this.setSelectedItem(select);
            TimeTable timetable = reader.readTimeTable();
            Object[] stops = timetable.stops().toArray();
            

            Stop departureStop = null;
            for(int i = 0; i < timetable.stops().size(); i++){

                //on fait la boucle jusqu'a ce que le stop aie le même nom que le nom passé en argument puis on sort immédiatement.
                if(stops[i].hashCode() == select.hashCode()){
                    departureStop = (Stop) stops[i];
                    break;
                }
            }
            setStartingStop(departureStop);
            startingStop= departureStop;
        }
        
    }
    
    private JComponent createCenterPanel(){
        final JViewport viewPort = new JViewport();
        viewPort.setView(tiledMapComponent);
        PointOSM startingPosOSM = INITIAL_POSITION.toOSM(tiledMapComponent.zoom());
        viewPort.setViewPosition(new Point(startingPosOSM.roundedX(), startingPosOSM.roundedY()));
        final Point currentMouseLocation = new Point();
        final Point currentViewLocation = new Point();
        
        
        final JPanel copyrightPanel = createCopyrightPanel();

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));

        layeredPane.add(viewPort, new Integer(0));
        layeredPane.add(copyrightPanel, new Integer(1));
        

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Rectangle newBounds = layeredPane.getBounds();
                viewPort.setBounds(newBounds);
                copyrightPanel.setBounds(newBounds);

                viewPort.revalidate();
                copyrightPanel.revalidate();
            }
        });

        //déplacement de la carte à la souris
        

        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                currentMouseLocation.setLocation(e.getLocationOnScreen());
                currentViewLocation.setLocation(viewPort.getViewPosition());

            }
        });
        
        
        layeredPane.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged (MouseEvent e) {
                
                Point locationOnScreen = e.getLocationOnScreen();
                Point viewPosition = viewPort.getViewPosition();
                
                viewPosition.setLocation(viewPosition.getX()+currentMouseLocation.getX()-locationOnScreen.getX(), viewPosition.getY()+currentMouseLocation.getY()-locationOnScreen.getY());
                currentMouseLocation.setLocation(locationOnScreen);
                ((TiledMapComponent) viewPort.getView()).setIsochronePosition((int)viewPosition.getX(), (int)viewPosition.getY());
                viewPort.setViewPosition(viewPosition);
                currentViewLocation.setLocation(viewPosition);
            }
        });
        
        // TODO zoom de la carte à la souris (molette)
        
        layeredPane.addMouseWheelListener(new  MouseWheelListener(){

            @Override
            public void mouseWheelMoved(MouseWheelEvent arg0) {
                // TODO Auto-generated method stub
                
                int rotation = arg0.getWheelRotation();//1 pour agrandir et -1 pour rétrécir
                Point mouseLocation = arg0.getPoint();

                int firstZoom =((TiledMapComponent) viewPort.getView()).zoom();
                
                int newZoom = (firstZoom - rotation);
                if(newZoom > 19){
                    newZoom = 19;
                }
                if (newZoom < 10){
                    newZoom =10;
                }
                ((TiledMapComponent) viewPort.getView()).setZoom(newZoom);
                int positionX=0;
                int positionY =0;
                if(newZoom > firstZoom){
                positionX = (int) ((int)mouseLocation.getX()/2+viewPort.getViewPosition().getX());
                positionY = (int) ((int)mouseLocation.getY()/2+viewPort.getViewPosition().getY());
                }
                else if(newZoom == firstZoom){
                    positionX = (int) (viewPort.getViewPosition().getX());
                    positionY = (int) (viewPort.getViewPosition().getY());
                }
                else{
                    positionX = (int) (-(int)mouseLocation.getX()+viewPort.getViewPosition().getX());
                    positionY = (int) (-(int)mouseLocation.getY()+viewPort.getViewPosition().getY());
                }
                //System.out.println("positionX: "+ positionX +"----"+ "positionY: "+ positionY);
                PointOSM beforeZoom = new PointOSM(firstZoom, positionX, positionY);
                PointOSM afterZoom = beforeZoom.atZoom(newZoom);
                ((TiledMapComponent)viewPort.getView()).setIsochronePosition((int)afterZoom.x(), (int)afterZoom.y());
                viewPort.setViewPosition(new Point((int)afterZoom.x(), (int)afterZoom.y()));
                //System.out.println(beforeZoom + "------"+ afterZoom);
                //System.out.println("Zoom niveau:" + newZoom);
                //System.out.println("Nombre de rotations:"+ rotation);
                //System.out.println("passage de ouf");
                
            }
            
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createCopyrightPanel() {
        Icon tlIcon = new ImageIcon(getClass().getResource("/images/tl-logo.png"));
        String copyrightText = "Données horaires 2013. Source : Transports publics de la région lausannoise / Carte : © contributeurs d'OpenStreetMap";
        JLabel copyrightLabel = new JLabel(copyrightText, tlIcon, SwingConstants.CENTER);
        copyrightLabel.setOpaque(true);
        copyrightLabel.setForeground(new Color(1f, 1f, 1f, 0.6f));
        copyrightLabel.setBackground(new Color(0f, 0f, 0f, 0.4f));
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

        JPanel copyrightPanel = new JPanel(new BorderLayout());
        copyrightPanel.add(copyrightLabel, BorderLayout.PAGE_END);
        copyrightPanel.setOpaque(false);
        return copyrightPanel;
    }

    private void start() {
        JFrame frame = new JFrame("Isochrone TL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        frame.getContentPane().add(createFrontPanel(), BorderLayout.PAGE_START);

        frame.pack();
        frame.setVisible(true);
        
       
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new IsochroneTL().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
