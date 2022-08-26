package com.yingenus.pocketchinese.view.keyboard;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import com.yingenus.pocketchinese.R;

public class PinyinPocketInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private Keyboard mQwerty;
    private Keyboard mCandidates_a, mCandidates_e, mCandidates_i,
            mCandidates_o, mCandidates_u, mCandidates_v;
    private Keyboard mCurCandidates;
    private KeyboardView mInputView;
    private KeyboardView mCandidatesView;

    private String mWordSeparator=" ";
    private StringBuffer mComposing =new StringBuffer();


    private int displayWidth;

    private boolean mAlreadySelected=false;
    private boolean mIsCandidateViewShow=false;


    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        if (mQwerty !=null){
            int maxWidth=getMaxWidth();
            if (displayWidth==maxWidth) return;
            displayWidth=maxWidth;
        }

        mQwerty =new Keyboard(getApplicationContext(),R.xml.qwerty);
        mCandidates_a =new Keyboard(getApplicationContext(),R.xml.pin_a);
        mCandidates_e =new Keyboard(getApplicationContext(),R.xml.pin_e);
        mCandidates_i =new Keyboard(getApplicationContext(),R.xml.pin_i);
        mCandidates_o =new Keyboard(getApplicationContext(),R.xml.pin_o);
        mCandidates_u =new Keyboard(getApplicationContext(),R.xml.pin_u);
        mCandidates_v =new Keyboard(getApplicationContext(),R.xml.pin_v);
    }

    @Override
    public View onCreateInputView() {
        KeyboardView pinKeyboard=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboard_pin_view,null);
        mInputView =pinKeyboard;
        pinKeyboard.setOnKeyboardActionListener(this);
        pinKeyboard.setKeyboard(mQwerty);
        return pinKeyboard;
    }

    @Override
    public View onCreateCandidatesView() {
        KeyboardView toneKeyboard=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboard_tone_view,null);
        mCandidatesView =toneKeyboard;
        toneKeyboard.setOnKeyboardActionListener(this);
        return toneKeyboard;
    }


    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart,
                                  int newSelEnd, int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidatesView();
            InputConnection ic = getCurrentInputConnection();
            if (ic!=null){
                ic.finishComposingText();
            }

        }
    }

    private void updateCandidatesView(){
        CharSequence text=getCurrentInputConnection().getTextBeforeCursor(1,0);
        if (text != null && text.length() != 0){

            char ch= Character.toLowerCase(text.charAt(0));

            if (!(!isVowel(ch)||mAlreadySelected)) {

                switch (ch) {
                    case 'a':
                        mCurCandidates = mCandidates_a;
                        break;
                    case 'e':
                        mCurCandidates = mCandidates_e;
                        break;
                    case 'i':
                        mCurCandidates = mCandidates_i;
                        break;
                    case 'o':
                        mCurCandidates = mCandidates_o;
                        break;
                    case 'u':
                        mCurCandidates = mCandidates_u;
                        break;
                    case 'ü':
                        mCurCandidates = mCandidates_v;
                        break;
                    default:
                        return;
                }

                mCandidatesView.setKeyboard(mCurCandidates);
                mCandidatesView.invalidate();
                mIsCandidateViewShow=true;
                setCandidatesViewShown(true);
                return;
            }
        }
        mIsCandidateViewShow=false;
        setCandidatesViewShown(false);
    }



    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode==Keyboard.KEYCODE_DELETE)
            handelBeckSpace();
        else if (primaryCode==Keyboard.KEYCODE_SHIFT)
            handelShift();
        else if (primaryCode==Keyboard.KEYCODE_DONE)
            handelDone();
        else if (primaryCode>=1000&&primaryCode<=1004)
            handel4Tone(primaryCode, keyCodes);
        else
            handelCharacter(primaryCode,keyCodes);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();

        mComposing.setLength(0);

        setCandidatesViewShown(false);
        if (mInputView !=null){
            mInputView.closing();
            mCandidatesView.closing();//new
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        mComposing.setLength(0);
    }

    @Override
    public void onText(CharSequence text) {

    }

    private void handelShift(){
        if (mInputView ==null)
            return;
        boolean shifted=!mQwerty.isShifted();

        mQwerty.setShifted(shifted);

        mCandidates_a.setShifted(shifted);
        mCandidates_e.setShifted(shifted);
        mCandidates_i.setShifted(shifted);
        mCandidates_o.setShifted(shifted);
        mCandidates_u.setShifted(shifted);
        mCandidates_v.setShifted(shifted);

        mInputView.invalidateAllKeys();
        if (mCandidatesView!=null)
            mCandidatesView.invalidateAllKeys();
    }

    private void handelBeckSpace(){
        int length= mComposing.length();
        if (length>1){
            mComposing.delete(length-1,length);
            getCurrentInputConnection().setComposingText(mComposing,1);
        }
        else if (length>0){
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("",1);
        }else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        mAlreadySelected=false;
        updateCandidatesView();
    }
    private void handelDone(){
        keyDownUp(KeyEvent.KEYCODE_ENTER);
    }

    private void handel4Tone(int primaryCode, int[] keyCodes){
        char ch='Q';
        if (mCurCandidates == mCandidates_a)
            ch=candidates2charA((primaryCode));
        else if (mCurCandidates == mCandidates_e)
            ch=candidates2charE((primaryCode));
        else if (mCurCandidates == mCandidates_i)
            ch=candidates2charI((primaryCode));
        else if (mCurCandidates == mCandidates_o)
            ch=candidates2charO((primaryCode));
        else if (mCurCandidates == mCandidates_u)
            ch=candidates2charU((primaryCode));
        else if (mCurCandidates == mCandidates_v)
            ch=candidates2charV((primaryCode));

        String str=String.valueOf(ch);

        if (mCandidatesView.isShifted()){
            str=str.toUpperCase();
        }

        int length= mComposing.length();

        if (length>1){
            mComposing.delete(length-1,length);
            mComposing.append(str);
            getCurrentInputConnection()
                    .setComposingText(mComposing,1);
        }
        else if (length>0){
            mComposing.setLength(0);
            mComposing.append(str);
            getCurrentInputConnection().commitText(mComposing,1);
        } if (length==0){
            CharSequence text=getCurrentInputConnection().getTextBeforeCursor(1,0);
            if (text!=null) {
                InputConnectionWrapper wrapper = new InputConnectionWrapper(getCurrentInputConnection(), false);
                wrapper.deleteSurroundingText(1, 0);
                mComposing.setLength(0);
                mComposing.append(str);
                wrapper.commitText(mComposing, 1);
            }
        }
        mAlreadySelected=true;
        updateCandidatesView();
    }

    private void handelCharacter(int primaryCode, int[] keyCodes ){
        if(isInputViewShown()){
            if (mInputView.isShifted())
                primaryCode=Character.toUpperCase((char)primaryCode);
        }
        if (isAlphabet(primaryCode)){
            String str=String.valueOf(convert2pin((char)primaryCode));
            mComposing.append(str);
            getCurrentInputConnection().setComposingText(mComposing,1);
        }
        else {
            String str = String.valueOf((char) primaryCode);
            mComposing.append(str);
            getCurrentInputConnection().setComposingText(mComposing, 1);
        }
        mAlreadySelected=false;
        updateCandidatesView();
    }

    private void keyDownUp(int code){
        getCurrentInputConnection()
                .sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));
        getCurrentInputConnection()
                .sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,code));
    }


    private boolean isAlphabet(int code){
        return Character.isAlphabetic(code);
    }
    private boolean isVowel(int code){
        char ch=(char)code;
        return ch=='a'||ch=='e'||ch=='i'||ch=='o'||ch=='u'||ch=='ü'||
                ch=='A'||ch=='E'||ch=='I'||ch=='O'||ch=='U'||ch=='Ü';
    }

    private char convert2pin(char ch){
        if(ch=='v')
            return 'ü';
        if (ch=='V')
            return 'Ü';
        else
            return ch;
    }


    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    private char candidates2charA(int code){
        if (code==1001)
            return 'ā';
        if (code==1002)
            return 'á';
        if (code==1003)
            return 'ǎ';
        if (code==1004)
            return 'à';
        return 'a';
    }
    private char candidates2charE(int code){
        if (code==1001)
            return 'ē';
        if (code==1002)
            return 'é';
        if (code==1003)
            return 'ě';
        if (code==1004)
            return 'è';
        return 'e';
    }
    private char candidates2charI(int code){
        if (code==1001)
            return 'ī';
        if (code==1002)
            return 'í';
        if (code==1003)
            return 'ǐ';
        if (code==1004)
            return 'ì';
        return 'i';
    }
    private char candidates2charO(int code){
        if (code==1001)
            return 'ō';
        if (code==1002)
            return 'ó';
        if (code==1003)
            return 'ǒ';
        if (code==1004)
            return 'ò';
        return 'o';
    }
    private char candidates2charU(int code){
        if (code==1001)
            return 'ū';
        if (code==1002)
            return 'ú';
        if (code==1003)
            return 'ǔ';
        if (code==1004)
            return 'ù';
        return 'u';
    }
    private char candidates2charV(int code){
        if (code==1001)
            return 'ǖ';
        if (code==1002)
            return 'ǘ';
        if (code==1003)
            return 'ǚ';
        if (code==1004)
            return 'ǜ';
        return 'ü';
    }
}
