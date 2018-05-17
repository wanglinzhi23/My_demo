package intellif.oauth;


/**
 * A strategy pattern for logic handling access times control
 * 
 * @author simon_zhang
 *
 */
public interface ApiLimitMethodHanler {

    /**
     * 
     * @param limitInfo contains verify info 
     * @return
     */
    public boolean apiLimitHanler(Object limitInfo);

}
