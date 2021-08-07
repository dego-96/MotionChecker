package jp.mydns.dego.zanzo.VideoPlayer;

public interface VideoChangeListener {

    void onDurationChanged(int duration);

    void setVisibilities(VideoDecoder.DecoderStatus status);
}
