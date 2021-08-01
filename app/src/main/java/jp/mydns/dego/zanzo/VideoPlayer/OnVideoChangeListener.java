package jp.mydns.dego.zanzo.VideoPlayer;

public interface OnVideoChangeListener {

    void onDurationChanged(int duration);

    void setVisibilities(VideoDecoder.DecoderStatus status);
}
