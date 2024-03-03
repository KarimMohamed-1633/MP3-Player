import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame{

    //Color Configurations
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    //allow us to use file explorer in our app
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;

    private JPanel playbackBtns;
    private JSlider playbackSlider;


    //This is a constructor method for the class, initialisng the components of the GUI
    public MusicPlayerGUI(){
        //calls JFrame constructor (superclass) to configure out gui and set the title header to "Music Player"
        super("Music Player");

        //Set the width and height
        setSize(400,600);

        //end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //launch the app at the center of the screen
        setLocationRelativeTo(null);

        //prevent the app from being resized
        setResizable(false);

        //set layout to null which allows us to control the (x,y) coordinates of our components
        //and also set the height and width
        setLayout(null);


        //change the frame color
        getContentPane().setBackground(FRAME_COLOR);


        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();

        //set a default path for file explorer
        jFileChooser.setCurrentDirectory(new File("out/production/MP3MusicPlayer/assets"));



        //filter file chooser only to see .mp3 files
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));


        addGuiComponents();
    }

    private void addGuiComponents(){
        //add toolbar
        addToolbar();

        //load record image
        JLabel songImage = new JLabel(loadingImage("src/assets/drive-download-20240225T155456Z-001/record.png"));
        songImage.setBounds(0, 50, getWidth()-20,225 );
        add(songImage);


        //song title
        songTitle = new JLabel(("Song Title"));
        songTitle.setBounds(0, 285, getWidth()-10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth()-10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);


        //playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(Color.WHITE);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //when the user is holding the mouse ticker we want to music to pause
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //when the user drops the tick
                JSlider source = (JSlider) e.getSource();


                //get the fram value from where the user wants  to playback to
                int frame = source.getValue();

                //update the current music frame to this frame in the music player
                musicPlayer.setCurrentFrame(frame);

                //update current time as milli as well
                musicPlayer.setCurrentTimeInMilli((int)(frame / (2.08*musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                //resume the song
                musicPlayer.playCurrentSong();


                //toggle on pause button / toggle on play button
                enablePauseButtonDisablePlayButton();


            }
        });


        add(playbackSlider);

        //playback buttons (i.e previous, play, next)
        addPlaybackBtns();


    }

    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0,0,getWidth(), 20);

        //prevent toolbar from being moved
        toolBar.setFloatable(false);


        //add dropdown menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);


        //now we will add a song menu where we will place the loading song option
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        //add the "Load Song" item in the songMenu
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //an integer is returned to us to let us know what the user did
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();


                //this means that we are also checking to see if the user pressed the "open" button
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    //create a song obj based on selected file
                    Song song = new Song(selectedFile.getPath());

                    //load song in music player
                    musicPlayer.loadSong(song);

                    //update song title and artist
                    updateSongTitleAndArtist(song);

                    //update playback slider
                    updatePlaybackSlider(song);


                    //toggle on pause button and toggle off pause button
                    enablePauseButtonDisablePlayButton();



                }
            }
        });
        songMenu.add(loadSong);

        //adding the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        //adding the items to the playlist menu
        JMenuItem playlistItems = new JMenuItem("Create Playlist");
        playlistItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //load music playlist dialog
                new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);

            }
        });

        playlistMenu.add(playlistItems);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
                jFileChooser.setCurrentDirectory(new File("src/assets/drive-download-20240225T155456Z-001"));

                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    //stop music
                    musicPlayer.stopSong();

                    //load playlist
                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        playlistMenu.add(loadPlaylist);


        add(toolBar);

    }


    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth()-10, 80);
        playbackBtns.setBackground(null);

        //previous buttn
        JButton prevButton = new JButton(loadingImage("src/assets/drive-download-20240225T155456Z-001/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to previous song
                musicPlayer.prevSong();

            }
        });
        playbackBtns.add(prevButton);


        //play button
        JButton playButton = new JButton(loadingImage("src/assets/drive-download-20240225T155456Z-001/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                enablePauseButtonDisablePlayButton();

                //play or resume song
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton);


        //pause button
        JButton pauseButton = new JButton(loadingImage("src/assets/drive-download-20240225T155456Z-001/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                //pause song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        //next button
        JButton nextButton = new JButton((loadingImage("src/assets/drive-download-20240225T155456Z-001/next.png")));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to next song
                musicPlayer.nextSong();

            }
        });
        playbackBtns.add(nextButton);

        add(playbackBtns);

    }

    //This will be used to update our slider from the music player class
    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }


    public void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }




    public void updatePlaybackSlider(Song song){
        //uopdate max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        //create the song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        //beginning will be 00:00
        JLabel labelBeginning  = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        //end will vary depending on the song
        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);


        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);

    }



    public void enablePauseButtonDisablePlayButton(){

        //retrieve reference to play button from playbackBtns panel
        //think of Jpanel as an array and we know the order which we stored the buttons in so we can indicate the index
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);


        //turn on pasue button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);

    }


    public void enablePlayButtonDisablePauseButton(){

        //retrieve reference to play button from playbackBtns panel
        //think of Jpanel as an array and we know the order which we stored the buttons in so we can indicate the index
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);


        //turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);

    }




    //This is a private method that takes a String parameter imagePath, which represents the path to the image file.
    private ImageIcon loadingImage(String imagePath){

        //try and catch is used to handle exceptions that may occur
        // during the image loading process. If any exception occurs,
        // it will be caught, and the stack trace will be printed.
        try{
            //read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));

            //returns an image icon so that our component can render the image
            return new ImageIcon(image);
        }catch(Exception e){
            e.printStackTrace();
        }

        //could not find resource
        return null;
    }

}
