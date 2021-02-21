package jp.mydns.dego.motionchecker.VideoPlayer;

public interface OnVideoChangeListener {
    void onDurationChanged(int duration);
    void setVisibilities(VideoDecoder.DecoderStatus status);
}
