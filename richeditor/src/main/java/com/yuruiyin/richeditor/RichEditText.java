package com.yuruiyin.richeditor;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.hanks.lineheightedittext.LineHeightEditText;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yuruiyin.richeditor.callback.OnImageClickListener;
import com.yuruiyin.richeditor.config.AppConfig;
import com.yuruiyin.richeditor.enumtype.FileTypeEnum;
import com.yuruiyin.richeditor.enumtype.ImageTypeMarkEnum;
import com.yuruiyin.richeditor.enumtype.RichTypeEnum;
import com.yuruiyin.richeditor.ext.LongClickableLinkMovementMethod;
import com.yuruiyin.richeditor.model.BlockImageSpanVm;
import com.yuruiyin.richeditor.model.RichEditorBlock;
import com.yuruiyin.richeditor.model.StyleBtnVm;
import com.yuruiyin.richeditor.span.BlockImageSpan;
import com.yuruiyin.richeditor.undoredo.UndoRedoHelper;
import com.yuruiyin.richeditor.utils.BitmapUtil;
import com.yuruiyin.richeditor.utils.ClipboardUtil;
import com.yuruiyin.richeditor.utils.FileUtil;
import com.yuruiyin.richeditor.utils.LogUtil;
import com.yuruiyin.richeditor.utils.ViewUtil;
import com.yuruiyin.richeditor.utils.WindowUtil;

import java.io.File;
import java.util.List;

/**
 * Title: 自定义EditText，可监听光标位置变化
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichEditText extends LineHeightEditText {

    private static final String TAG = "RichEditText";

    // 宽度撑满编辑区的ImageSpan需要减去的一个值，为了防止ImageSpan碰到边界导致的重复绘制的问题
    private static final int IMAGE_SPAN_MINUS_VALUE = 6;

    private int imageSpanPaddingTop;
    private int imageSpanPaddingBottom;
    private int imageSpanPaddingLeft;
    private int imageSpanPaddingRight;

    // 是否显示视频标识
    private boolean gIsShowVideoMark;
    // 视频标识图标资源id
    private int gVideoMarkResourceId;

    // 是否显示gif标识
    private boolean gIsShowGifMark;
    // 是否显示长图标识
    private boolean gIsShowLongImageMark;

    // 图片和视频封面的圆角大小
    private int gImageRadius;

    private int screenWidth;

    // 标题字体大小
    private int gHeadlineTextSize;

    /**
     * EditText的宽度
     */
    public static int gRichEditTextWidthWithoutPadding;

    private RichInputConnectionWrapper mRichInputConnection;

    private Activity mActivity;

    private RichUtils mRichUtils;

    private UndoRedoHelper undoRedoHelper;

    public interface OnSelectionChangedListener {
        /**
         * 光标位置改变回调
         *
         * @param curPos 新的光标位置
         */
        void onChange(int curPos);
    }

    /**
     * EditText监听复制、粘贴、剪切事件回调的接口
     */
    public interface IClipCallback {
        /**
         * 剪切回调
         */
        void onCut();

        /**
         * 复制回调
         */
        void onCopy();

        /**
         * 粘贴回调
         */
        void onPaste();
    }

    /**
     * 光标位置变化监听器
     */
    private OnSelectionChangedListener mOnSelectionChangedListener;

    public RichEditText(Context context) {
        super(context);
        init(context, null);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RichEditText);
            gIsShowVideoMark = ta.getBoolean(R.styleable.RichEditText_editor_show_video_mark, true);
            gVideoMarkResourceId = ta.getResourceId(R.styleable.RichEditText_editor_video_mark_resource_id, R.drawable.default_video_icon);
            gIsShowGifMark = ta.getBoolean(R.styleable.RichEditText_editor_show_gif_mark, true);
            gIsShowLongImageMark = ta.getBoolean(R.styleable.RichEditText_editor_show_long_image_mark, true);

            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            gImageRadius = (int) ta.getDimension(R.styleable.RichEditText_editor_image_radius, 0);

            float defHeadlineTextSize = context.getResources().getDimension(R.dimen.rich_editor_headline_text_size);
            gHeadlineTextSize = (int) ta.getDimension(R.styleable.RichEditText_editor_headline_text_size, defHeadlineTextSize);

            ta.recycle();
        }

        mActivity = (Activity) context;
        if (mActivity == null) {
            LogUtil.e(TAG, "activity is null");
            return;
        }

        imageSpanPaddingTop = (int) mActivity.getResources().getDimension(R.dimen.rich_editor_image_span_padding_top);
        imageSpanPaddingBottom = (int) mActivity.getResources().getDimension(R.dimen.rich_editor_image_span_padding_bottom);
        imageSpanPaddingLeft = (int) mActivity.getResources().getDimension(R.dimen.rich_editor_image_span_padding_left);
        imageSpanPaddingRight = (int) mActivity.getResources().getDimension(R.dimen.rich_editor_image_span_padding_right);

        mRichInputConnection = new RichInputConnectionWrapper(null, true);
        setMovementMethod(new LongClickableLinkMovementMethod());
        requestFocus();
        setSelection(0);

        mRichUtils = new RichUtils(mActivity, this);

        screenWidth = WindowUtil.getScreenSize(mActivity)[0];
    }

    public void undo() {
        if (undoRedoHelper != null) {
            undoRedoHelper.undo();
        }
    }

    public void redo() {
        if (undoRedoHelper != null) {
            undoRedoHelper.redo();
        }
    }

    private int getWidthWithoutPadding() {
        int editTextMeasureWidth = getMeasuredWidth();
        if (editTextMeasureWidth <= 0) {
            // 可能是编辑器还不可见, 则直接设置编辑器的宽度为屏幕的宽度
            editTextMeasureWidth = screenWidth;
        }
        return editTextMeasureWidth - getPaddingLeft() - getPaddingRight() - IMAGE_SPAN_MINUS_VALUE;
    }

    public void initStyleButton(StyleBtnVm styleBtnVm) {
        mRichUtils.initStyleButton(styleBtnVm);
    }

    /**
     * 清空编辑器的内容
     */
    public void clearContent() {
        setText("");
        requestFocus();
        setSelection(0);
    }

    /**
     * 插入整段文本(可能是普通文本、段落样式文本（标题或引用）)
     * 使用场景：如恢复草稿
     *
     * @param richEditorBlock
     */
    public void insertBlockText(RichEditorBlock richEditorBlock) {
        SpannableString spanStringContent = new SpannableString(richEditorBlock.getText() + "\n");
        String blockType = richEditorBlock.getBlockType();
        switch (blockType) {
            case RichTypeEnum.BLOCK_NORMAL_TEXT:
                mRichUtils.insertNormalTextBlock(spanStringContent, richEditorBlock.getInlineStyleEntityList());
                break;
            case RichTypeEnum.BLOCK_HEADLINE:
            case RichTypeEnum.BLOCK_QUOTE:
                mRichUtils.insertBlockSpanText(blockType, spanStringContent, richEditorBlock.getInlineStyleEntityList());
                break;
        }
    }

    /**
     * 控制视频、gif、长图标识的显示和隐藏
     *
     * @param imageItemView    包裹图标的外层View
     * @param blockImageSpanVm 相关实体
     */
    private void setMarkIconVisibility(View imageItemView, BlockImageSpanVm blockImageSpanVm) {
        ImageView ivVideoIcon = imageItemView.findViewById(R.id.ivVideoIcon);
        TextView tvGifOrLongImageMark = imageItemView.findViewById(R.id.tvGifOrLongImageMark);

        // 控制视频、gif、长图标识图标的显示和隐藏
        ivVideoIcon.setVisibility(GONE);
        tvGifOrLongImageMark.setVisibility(GONE);

        // 处理视频
        if (blockImageSpanVm.isVideo() && gIsShowVideoMark && gVideoMarkResourceId != 0) {
            // 视频封面，显示视频标识
            Drawable videoIconDrawable = AppCompatResources.getDrawable(mActivity, gVideoMarkResourceId);
            if (videoIconDrawable != null) {
                ivVideoIcon.setVisibility(VISIBLE);
                ivVideoIcon.setImageDrawable(videoIconDrawable);
                ViewGroup.LayoutParams layoutParams = ivVideoIcon.getLayoutParams();
                layoutParams.width = videoIconDrawable.getIntrinsicWidth();
                layoutParams.height = videoIconDrawable.getIntrinsicHeight();
            }
            return;
        }


        // 处理长图
        if (blockImageSpanVm.isLong() && gIsShowLongImageMark) {
            // 长图，显示长图标识
            tvGifOrLongImageMark.setVisibility(VISIBLE);
            tvGifOrLongImageMark.setText(ImageTypeMarkEnum.LONG);
            return;
        }

        // 处理gif
        if (blockImageSpanVm.isGif() && gIsShowGifMark) {
            // gif, 显示gif标识
            tvGifOrLongImageMark.setVisibility(VISIBLE);
            tvGifOrLongImageMark.setText(ImageTypeMarkEnum.GIF);
        }
    }

    /**
     * 在插入blockImage之前，先删除被光标选中的区域
     */
    private void removeSelectedContent() {
        Editable editable = getEditableText();
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();

        if (selectionStart >= selectionEnd) {
            return;
        }

        editable.delete(selectionStart, selectionEnd);
    }

    public void insertBlockImage(Drawable drawable, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        removeSelectedContent();

        int originWidth = drawable.getIntrinsicWidth();
        int originHeight = drawable.getIntrinsicHeight();
        blockImageSpanVm.setLong(originHeight > originWidth * AppConfig.IMAGE_MAX_HEIGHT_WIDTH_RATIO);

        // 这里减去一个值是为了防止部分手机（如华为Mate-10）ImageSpan右侧超出编辑区的时候，会导致ImageSpan被重复绘制的问题
        int editTextWidth = getWidthWithoutPadding();
        int imageWidth = blockImageSpanVm.getWidth() <= 0 ? originWidth : blockImageSpanVm.getWidth();
        int resImageWidth = Math.min(imageWidth, editTextWidth);
        int imageMaxHeight = blockImageSpanVm.getMaxHeight() <= 0 ? originHeight : blockImageSpanVm.getMaxHeight();
        int resImageHeight = (int) (originHeight * 1.0 / originWidth * resImageWidth);
        resImageHeight = Math.min(resImageHeight, imageMaxHeight);
        // 控制显示出来的图片的高度不会大于宽度的3倍
        double maxHeightWidthRadio = AppConfig.IMAGE_MAX_HEIGHT_WIDTH_RATIO;
        resImageHeight = resImageHeight > resImageWidth * maxHeightWidthRadio
                ? (int) (resImageWidth * maxHeightWidthRadio)
                : resImageHeight;

        View imageItemView = mActivity.getLayoutInflater().inflate(R.layout.rich_editor_image, null);
        RoundedImageView imageView = imageItemView.findViewById(R.id.image);
        imageView.setImageDrawable(drawable);
        // 设置圆角
        imageView.setCornerRadius(gImageRadius);

        // 控制视频、gif、长图标识的显示和隐藏
        setMarkIconVisibility(imageItemView, blockImageSpanVm);

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = resImageWidth;
        layoutParams.height = resImageHeight;

        ViewUtil.layoutView(
                imageItemView,
                resImageWidth + imageSpanPaddingLeft + imageSpanPaddingRight,
                resImageHeight + imageSpanPaddingTop + imageSpanPaddingBottom
        );

        BlockImageSpan blockImageSpan = new BlockImageSpan(
                mActivity, BitmapUtil.getBitmap(imageItemView), blockImageSpanVm
        );
        mRichUtils.insertBlockImageSpan(blockImageSpan);

        // 设置图片点击监听器
        blockImageSpan.setOnClickListener(onImageClickListener);
    }

    /**
     * 根据uri插入图片或视频封面
     *
     * @param uri                  文件uri
     * @param blockImageSpanVm     相关实体
     * @param onImageClickListener 图片点击事件监听器
     */
    public void insertBlockImage(Uri uri, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        if (uri == null) {
            LogUtil.e(TAG, "uri is null");
            return;
        }

        insertBlockImage(FileUtil.getFileRealPath(mActivity, uri), blockImageSpanVm, onImageClickListener);
    }

    /**
     * 根据文件路径插入图片或视频封面
     *
     * @param filePath             图片(或视频)文件路径，类似 /storage/emulated/0/Pictures/17173/1553236560146.jpg
     * @param blockImageSpanVm     相关实体
     * @param onImageClickListener 图片点击事件监听器
     */
    public void insertBlockImage(String filePath, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        if (TextUtils.isEmpty(filePath)) {
            LogUtil.e(TAG, "file path is empty");
            return;
        }

        try {
            File imageFile = new File(filePath);
            if (!imageFile.exists()) {
                LogUtil.e(TAG, "image file does not exist");
                return;
            }

            String fileType = FileUtil.getFileType(filePath);
            switch (fileType) {
                case FileTypeEnum.VIDEO:
                    Bitmap coverBitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    blockImageSpanVm.setVideo(true);
                    blockImageSpanVm.setGif(false);
                    insertBlockImage(coverBitmap, blockImageSpanVm, onImageClickListener);
                    break;
                case FileTypeEnum.STATIC_IMAGE:
                case FileTypeEnum.GIF:
                    blockImageSpanVm.setVideo(false);
                    if (FileTypeEnum.GIF.equals(fileType)) {
                        blockImageSpanVm.setGif(true);
                    } else {
                        blockImageSpanVm.setGif(false);
                    }
                    // 通过uri或path调用的可以断定为相册图片或视频，有添加圆角的需求
                    blockImageSpanVm.setPhoto(true);

                    int vmExpectWidth = blockImageSpanVm.getWidth();
                    int expectWidth = vmExpectWidth <= 0 ? gRichEditTextWidthWithoutPadding : vmExpectWidth;
                    Bitmap resBitmap = BitmapUtil.decodeSampledBitmapFromFilePath(filePath, expectWidth);
                    resBitmap = BitmapUtil.rotateBitmap(filePath, resBitmap);

                    Drawable drawable = new BitmapDrawable(mActivity.getResources(), resBitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    insertBlockImage(drawable, blockImageSpanVm, onImageClickListener);
                    break;
                default:
                    LogUtil.e(TAG, "file type is illegal");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "insert image fail");
        }
    }

    public void insertBlockImage(@DrawableRes int resourceId, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        try {
            Drawable drawable = AppCompatResources.getDrawable(mActivity, resourceId);
            insertBlockImage(drawable, blockImageSpanVm, onImageClickListener);
        } catch (Exception e) {
            LogUtil.e(TAG, "Unable to find resource: " + resourceId);
        }
    }

    public void insertBlockImage(Bitmap bitmap, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        Drawable drawable = mActivity != null
                ? new BitmapDrawable(mActivity.getResources(), bitmap)
                : new BitmapDrawable(bitmap);
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, Math.max(width, 0), Math.max(height, 0));
        insertBlockImage(drawable, blockImageSpanVm, onImageClickListener);
    }

    /**
     * 获取编辑器中的内容
     *
     * @return 编辑的内容
     */
    public List<RichEditorBlock> getContent() {
        return mRichUtils.getContent();
    }

    public RichUtils getRichUtils() {
        return mRichUtils;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        gRichEditTextWidthWithoutPadding = getWidthWithoutPadding();
    }

    /**
     * 设置软键盘删除按键监听器
     *
     * @param backspaceListener 软键盘删除按键监听器
     */
    protected void setBackspaceListener(RichInputConnectionWrapper.BackspaceListener backspaceListener) {
        mRichInputConnection.setBackspaceListener(backspaceListener);
    }

    /**
     * 注册光标位置监听器
     *
     * @param listener 光标位置变化监听器
     */
    protected void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.mOnSelectionChangedListener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onChange(selEnd);
        }
    }

    /**
     * 处理粘贴
     */
    private void handlePaste() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Editable editable = getEditableText();
        editable.delete(selectionStart, selectionEnd);
        selectionStart = getSelectionStart();
        mRichUtils.insertStringIntoEditText(ClipboardUtil.getInstance(mActivity).getClipboardText(), selectionStart);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.cut:
                if (mActivity instanceof IClipCallback) {
                    ((IClipCallback) mActivity).onCut();
                }
                break;
            case android.R.id.copy:
                LogUtil.d(TAG, "getSelectionStart: " + getSelectionStart() + ", getSelectionEnd: " + getSelectionEnd());
                if (mActivity instanceof IClipCallback) {
                    ((IClipCallback) mActivity).onCopy();
                }
                break;
            case android.R.id.paste:
                if (mActivity instanceof IClipCallback) {
                    ((IClipCallback) mActivity).onPaste();
                }

                handlePaste();
                return true;
            default:
                break;
        }

        return super.onTextContextMenuItem(id);
    }

    /**
     * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
     * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        mRichInputConnection.setTarget(super.onCreateInputConnection(outAttrs));
        return mRichInputConnection;
    }

    public int getVideoMarkResourceId() {
        return gVideoMarkResourceId;
    }

    public void setVideoMarkResourceId(int videoMarkResourceId) {
        this.gVideoMarkResourceId = videoMarkResourceId;
    }

    public boolean isShowVideoMark() {
        return gIsShowVideoMark;
    }

    public void setIsShowVideoMark(boolean isShowVideoMark) {
        this.gIsShowVideoMark = isShowVideoMark;
    }

    public boolean isShowGifMark() {
        return gIsShowGifMark;
    }

    public void setIsShowGifMark(boolean isShowGifMark) {
        this.gIsShowGifMark = isShowGifMark;
    }

    public boolean isShowLongImageMark() {
        return gIsShowLongImageMark;
    }

    public void setIsShowLongImageMark(boolean isShowLongImageMark) {
        this.gIsShowLongImageMark = isShowLongImageMark;
    }

    public int getHeadlineTextSize() {
        return gHeadlineTextSize;
    }

    public void setHeadlineTextSize(int headlineTextSize) {
        this.gHeadlineTextSize = headlineTextSize;
    }

    public void setUndoRedoEnable(boolean enableUndoRedo) {
        if (enableUndoRedo) {
            undoRedoHelper = new UndoRedoHelper(this);
        }
    }

    public boolean isUndoRedoEnable() {
        return undoRedoHelper != null;
    }

}
