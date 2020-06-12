package container.callback;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;

import java.io.Closeable;
import java.io.IOException;

public class PullImageResultCallBack extends ResultCallback.Adapter<PullResponseItem>{
}
