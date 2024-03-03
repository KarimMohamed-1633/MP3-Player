import javax.swing.*;
public class App {

    public static void main(String[] args){

        //This method ensures that Swing components are created
        // and updated on the Event Dispatch Thread (EDT), which is
        // the thread responsible for handling GUI events. This is necessary
        // to prevent concurrency issues in Swing applications.
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                ////This line creates an instance of the MusicPlayerGUI
                // class and makes it visible by calling setVisible(true).
                // The invokeLater() method ensures that this code is executed on the EDT.


                new MusicPlayerGUI().setVisible(true);


//                Song song = new Song("/Users/karimmohamed/Desktop/MP3MusicPlayer/out/production/MP3MusicPlayer/assets/Drake---Slime-You-Out-Lyrics-ft-SZA(musicdownload.cc).mp3");
//                System.out.println(song.getSongTitle());
//                System.out.println(song.getSongArtist());

            }
        });
    }
}
