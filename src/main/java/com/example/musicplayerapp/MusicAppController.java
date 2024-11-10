package com.example.musicplayerapp;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import javafx.scene.control.Slider;

import java.sql.*;
import java.util.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.*;
import javafx.util.Duration;

public class MusicAppController implements Initializable {
    @FXML
    private Button btnCreatePlayLiist;

    @FXML
    private Button btnFavourite;
    @FXML
    private TableColumn<FavouriteSongs, String> colFavoriteArtist;

    @FXML
    private TableColumn<FavouriteSongs, String> colFavoriteDuration;

    @FXML
    private TableColumn<FavouriteSongs, String> colFavoriteTitle;

    @FXML
    private Button btnHome, btnFavorite, btn_unSaveFavorite;

    @FXML
    private TableColumn<HomeSong, String> colSongArtist;

    @FXML
    private TableColumn<HomeSong, String> colSongDuration;

    @FXML
    private TableColumn<HomeSong, Integer> colSongNumber;

    @FXML
    private TableColumn<HomeSong, String> colSongTitle;

    @FXML
    private AnchorPane homeForm, formHome, formFavorite;

    @FXML
    private ImageView imgSong;

    @FXML
    private TableView<HomeSong> listHomeSong;
    @FXML
    private TableView<FavouriteSongs> fvrSongList;

    @FXML
    private Label songLabel, fvrSongName;

    @FXML
    private Label songName;
    @FXML
    private ImageView imgFvrSong;

    @FXML
    private ProgressBar songProgressBar;

    @FXML
    private Label timeSong;

    @FXML
    private Slider volumeSliderBar;

    private HomeSong currentSong;

    private File directory;
    private File[] files;
    private Media media;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songsHome, imgHomeSong, songFavorite, imgFavorite;
    private int songNumber;
    private Timer timer;
    private TimerTask timerTask;
    private boolean running;
    private Duration duration;
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private Image image;
    private boolean isHome, isFavorite;
    private int songCurrentHome, songCurrentFavorite, imgCurrentHome, imgCurrentFavorite;
    File file, fileImg, fileFvr, fileImgFvr;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        showListHomeSongs();
        showListFavoriteSongs();

    }

    public void changeForm(ActionEvent actionEvent){
        if(actionEvent.getSource() == btnHome){
            formHome.setVisible(true);
            formFavorite.setVisible(false);
            btnFavourite.setVisible(true);
            btn_unSaveFavorite.setVisible(false);
            if(mediaPlayer != null)
                mediaPlayer.pause();
            isHome = true;


        }
        else if(actionEvent.getSource() == btnFavorite){
            formFavorite.setVisible(true);
            formHome.setVisible(false);
            btn_unSaveFavorite.setVisible(true);
            btnFavourite.setVisible(false);
            if(mediaPlayer != null)
                mediaPlayer.pause();
            isHome = false;


        }
    }


    public ObservableList<FavouriteSongs> favoriteData(){
        songFavorite = new ArrayList<File>();
        imgFavorite = new ArrayList<File>();
        ObservableList<FavouriteSongs> listFavoriteSongs= FXCollections.observableArrayList();
        String sql = "SELECT * FROM favorite";

        try {
            FavouriteSongs favouriteSongs;

            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            while(result.next()){
                favouriteSongs  = new FavouriteSongs(result.getString("title"),
                        result.getString("artist"), result.getString("Duration"), result.getString("img"), result.getString("song"));
                listFavoriteSongs.add(favouriteSongs);
                fileFvr = new File(favouriteSongs.getSong());
                fileImgFvr = new File(favouriteSongs.getImg());
                if(file!=null){
                    songFavorite.add(fileFvr);
                    imgFavorite.add(fileImgFvr);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return listFavoriteSongs;
    }


    public void saveFavoriteSong(){
        String sql = "INSERT INTO favorite VALUES(?,?,?,?,?)";
        connect = Database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, GetSong.fvrTitleSong);
            prepare.setString(2, GetSong.fvrArtist);
            prepare.setString(3, GetSong.fvrDuration);
            prepare.setString(4, GetSong.fvrImage);
            prepare.setString(5, GetSong.fvrSong);
            prepare.executeUpdate();
            showListFavoriteSongs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private ObservableList<FavouriteSongs> listFavoriteSongs;

    public void showListFavoriteSongs(){
        listFavoriteSongs = favoriteData();
        colFavoriteTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colFavoriteArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colFavoriteDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));

        fvrSongList.setItems(listFavoriteSongs);
    }
    public void unSaveFavoriteSong(){
        String sql = "DELETE FROM favorite WHERE title= '"+GetSong.fvrTitleSong +"'";
        connect = Database.connectDb();

        try{
            statement = connect.createStatement();
            statement.executeUpdate(sql);
            showListFavoriteSongs();

        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
    private ArrayList<File> fileSongHome;


    public ObservableList<HomeSong> dataList(){
        songsHome = new ArrayList<File>();
        imgHomeSong = new ArrayList<File>();
        Database connection = new Database();
        connect = connection.connectDb();

        ObservableList<HomeSong> listHomeSongs= FXCollections.observableArrayList();

        String sql = "SELECT * FROM homesong";

        try {
            HomeSong homeSong;

            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            while(result.next()){
                homeSong  = new HomeSong(result.getInt("id"), result.getString("title"),
                        result.getString("artist"), result.getString("duration"), result.getString("img"), result.getString("song"));
                listHomeSongs.add(homeSong);
//                System.out.println(result.getString("song"));
                file = new File(homeSong.getSong());
                fileImg = new File(homeSong.getImg());
                if(file!=null){
                    songsHome.add(file);
                    imgHomeSong.add(fileImg);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return listHomeSongs;
    }
    private ObservableList<HomeSong> listHomeSongs;

    public void showListHomeSongs(){
        listHomeSongs = dataList();
        colSongNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));

        listHomeSong.setItems(listHomeSongs);
    }
    public void selectFavoriteSong() {

        FavouriteSongs favouriteSongs = fvrSongList.getSelectionModel().getSelectedItem();
        int num = fvrSongList.getSelectionModel().getFocusedIndex();
        songCurrentFavorite = num;
        imgCurrentFavorite = num;
//        System.out.println(num);
        if ((num - 1) < -1)
            return;
        fvrSongName.setText(favouriteSongs.getTitle());
        songLabel.setText(favouriteSongs.getTitle());
        String uri = "file:" + favouriteSongs.getImg();
        File file = new File(favouriteSongs.getSong());
        image = new Image(uri, 214, 225, false, true);
        imgFvrSong.setImage(image);
        GetSong.takeSongTitle = favouriteSongs.getTitle();
        media = new Media(file.toURI().toString());
        if(mediaPlayer != null)
            mediaPlayer.pause();
        mediaPlayer = new MediaPlayer(media);
        GetSong.fvrTitleSong = favouriteSongs.getTitle();
        GetSong.fvrArtist = favouriteSongs.getArtist();
        GetSong.fvrDuration = favouriteSongs.getDuration();
        GetSong.fvrImage = favouriteSongs.getImg();
        GetSong.fvrSong = favouriteSongs.getSong();
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
                nextMedia();
            }
        });
        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues();
        });
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateValues();
            }
        });

        volumeSliderBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSliderBar.getValue() * 0.01);
            }
        });
    }
    public void selectHomeSong(){

        HomeSong songData = listHomeSong.getSelectionModel().getSelectedItem();
        int num = listHomeSong.getSelectionModel().getFocusedIndex();
        songCurrentHome = num;
        imgCurrentHome = num;

        if((num-1) <-1)
            return;
        songName.setText(songData.getTitle());
        songLabel.setText(songData.getTitle());
        String uri = "file:" + songData.getImg();
        File file = new File(songData.getSong());
        image = new Image(uri, 214, 225, false, true);
        imgSong.setImage(image);
        GetSong.takeSongTitle = songData.getTitle();
        media = new Media(file.toURI().toString());
        if(mediaPlayer != null)
            mediaPlayer.pause();
        mediaPlayer = new MediaPlayer(media);
        GetSong.fvrTitleSong = songData.getTitle();
        GetSong.fvrArtist = songData.getArtist();
        GetSong.fvrDuration = songData.getDuration();
        GetSong.fvrImage = songData.getImg();
        GetSong.fvrSong = songData.getSong();
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateValues();
            }
        });

        volumeSliderBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSliderBar.getValue()*0.01);
            }
        });
    }
    protected void updateValues() {
        duration = mediaPlayer.getMedia().getDuration();
        if (timeSong != null && songProgressBar != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    timeSong.setText(formatTime(currentTime, duration));
                    songProgressBar.setDisable(duration.isUnknown());
                }
            });
        }
    }
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
    @FXML
    public void playMedia(){
        beginTimer();
        if(mediaPlayer!=null)
            mediaPlayer.play();
        updateValues();
    }
    @FXML
    public void pauseMedia(){
        cancelTimer();
        if(mediaPlayer!=null)
            mediaPlayer.pause();
        updateValues();
    }
    @FXML
    public void replayMedia(){
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
        updateValues();
    }
    @FXML
    public void previousMedia(){
        if(isHome) {
            if (songCurrentHome > 0 && imgCurrentHome >0) {
                imgCurrentHome--;
                songCurrentHome--;


                if (mediaPlayer != null)
                    mediaPlayer.stop();
                if (running)
                    cancelTimer();
                System.out.println(songCurrentHome);
                media = new Media(songsHome.get(songCurrentHome).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songsHome.get(songCurrentHome).getName());
                songName.setText(songsHome.get(songCurrentHome).getName());
                System.out.println(imgCurrentHome);
                String uri = "file:" + imgHomeSong.get(imgCurrentHome).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgSong.setImage(image);
            }
            else{
                songCurrentHome = songsHome.size() -1 ;
                imgCurrentHome = imgHomeSong.size()-1;
                System.out.println(songCurrentHome);
                System.out.println(imgCurrentHome);
                if(mediaPlayer!=null)
                    mediaPlayer.stop();
                if(running)
                    cancelTimer();
                media = new Media(songsHome.get(songCurrentHome).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songsHome.get(songCurrentHome).getName());
                songName.setText(songsHome.get(songCurrentHome).getName());
                String uri = "file:" + imgHomeSong.get(imgCurrentHome).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgSong.setImage(image);
            }
            duration = mediaPlayer.getMedia().getDuration();
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    updateValues();
                }
            });
            playMedia();
        }
        else  {
            if (songCurrentFavorite >0 && imgCurrentFavorite >0) {
                imgCurrentFavorite--;
                songCurrentFavorite--;
                if (mediaPlayer != null)
                    mediaPlayer.stop();
                if (running)
                    cancelTimer();

                media = new Media(songFavorite.get(songCurrentFavorite).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songFavorite.get(songCurrentFavorite).getName());
                fvrSongName.setText(songFavorite.get(songCurrentFavorite).getName());
                String uri = "file:" + imgFavorite.get(imgCurrentFavorite).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgFvrSong.setImage(image);

            }
            else{
                songCurrentFavorite = songFavorite.size() -1;
                imgCurrentFavorite = imgFavorite.size()-1;
                if (mediaPlayer != null)
                    mediaPlayer.stop();
                if(running)
                    cancelTimer();

                media = new Media(songFavorite.get(songCurrentFavorite).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songFavorite.get(songCurrentFavorite).getName());
                fvrSongName.setText(songFavorite.get(songCurrentFavorite).getName());
                String uri = "file:" + imgFavorite.get(imgCurrentFavorite).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgFvrSong.setImage(image);
            }
            duration = mediaPlayer.getMedia().getDuration();
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                nextMedia();
            });
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    updateValues();
                }
            });
            playMedia();
        }
    }
    @FXML
    public void nextMedia(){

        if(isHome) {
            if (songCurrentHome < songsHome.size() - 1 && imgCurrentHome <imgHomeSong.size() -1) {
                songCurrentHome++;
                imgCurrentHome++;
                System.out.println(songCurrentHome);
                System.out.println(imgCurrentHome);
                if (mediaPlayer != null)
                    mediaPlayer.stop();
                if (running)
                    cancelTimer();

                media = new Media(songsHome.get(songCurrentHome).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songsHome.get(songCurrentHome).getName());
                songName.setText(songsHome.get(songCurrentHome).getName());
                String uri = "file:" + imgHomeSong.get(imgCurrentHome).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgSong.setImage(image);

            }
                else{
                    songCurrentHome = 0;
                    imgCurrentHome = 0;
                    mediaPlayer.stop();

                    if(running)
                        cancelTimer();

                    media = new Media(songsHome.get(songCurrentHome).toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    songLabel.setText(songsHome.get(songCurrentHome).getName());
                    songName.setText(songsHome.get(songCurrentHome).getName());
                    String uri = "file:" + imgHomeSong.get(imgCurrentHome).getPath();
//                    System.out.println(uri);
                    image = new Image(uri, 214, 225, false, true);
                    imgSong.setImage(image);
            }
            duration = mediaPlayer.getMedia().getDuration();

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    updateValues();
                }
            });
            playMedia();

        }
        else{
            if (songCurrentFavorite < songFavorite.size() - 1 && imgCurrentFavorite < imgFavorite.size() -1)  {
                songCurrentFavorite++;
                imgCurrentFavorite++;
                if (mediaPlayer != null)
                    mediaPlayer.stop();
                if (running)
                    cancelTimer();

                media = new Media(songFavorite.get(songCurrentFavorite).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songFavorite.get(songCurrentFavorite).getName());
                fvrSongName.setText(songFavorite.get(songCurrentFavorite).getName());
                String uri = "file:" + imgFavorite.get(imgCurrentFavorite).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgFvrSong.setImage(image);

            }
            else{
                songCurrentFavorite = 0;
                imgCurrentFavorite=0;
                if (mediaPlayer != null)
                    mediaPlayer.stop();
                if(running)
                    cancelTimer();

                media = new Media(songFavorite.get(songCurrentFavorite).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(songFavorite.get(songCurrentFavorite).getName());
                fvrSongName.setText(songFavorite.get(songCurrentFavorite).getName());
                String uri = "file:" + imgFavorite.get(imgCurrentFavorite).getPath();
//                System.out.println(uri);
                image = new Image(uri, 214, 225, false, true);
                imgFvrSong.setImage(image);
            }
            duration = mediaPlayer.getMedia().getDuration();
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    nextMedia();
                }
            });
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    updateValues();
                }
            });
            playMedia();
      }
    }


    @FXML
    public void beginTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();

                songProgressBar.setProgress(current/end);
                if(current/end == 1){
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @FXML
    public void cancelTimer(){
        running = false;
        timer.cancel();
    }




}