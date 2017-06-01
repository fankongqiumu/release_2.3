package com.fanwe.library.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by Administrator on 2016/11/22.
 */

public class SDAnimSet extends SDAnim
{
    private AnimatorSet animatorSet;
    private SDAnimSet parentAnim;
    private SDAnimSet currAnim;

    private void initAnim(SDAnimSet anim)
    {
        View target = anim.getTargetView();
        if (target == null)
        {
            target = this.getTargetView();
            if (target == null)
            {
                if (parentAnim != null)
                {
                    target = parentAnim.getTargetView();
                }
            }
            anim.setTarget(target);
        }
    }

    private void setParentAnim(SDAnimSet parentAnim)
    {
        this.parentAnim = parentAnim;
    }

    private void saveAnim(SDAnimSet anim)
    {
        if (parentAnim != null)
        {
            anim.setParentAnim(parentAnim);
            parentAnim.saveAnim(anim);
        } else
        {
            if (this != anim)
            {
                anim.setParentAnim(this);
            }
            this.currAnim = anim;
        }
    }

    public SDAnimSet getSaveAnim()
    {
        if (parentAnim != null)
        {
            return parentAnim.getSaveAnim();
        } else
        {
            return this.currAnim;
        }
    }

    private void initParent()
    {
        if (animatorSet != null)
        {
            // 如果不是第一次初始化，需要设置新的对象，否则以前的第一次动画会被保留
            ObjectAnimator objectAnimator = new ObjectAnimator();
            objectAnimator.setTarget(getTargetView());
            setAnimator(objectAnimator);
        }
        animatorSet = new AnimatorSet();
        animatorSet.play(get());
        currAnim = this;
    }

    public SDAnimSet reset()
    {
        if (parentAnim != null)
        {
            return parentAnim.reset();
        } else
        {
            initParent();
            return this;
        }
    }

    public SDAnimSet with()
    {
        return with(new SDAnimSet());
    }

    public SDAnimSet with(View target)
    {
        SDAnimSet anim = new SDAnimSet();
        anim.setTarget(target);
        return with(anim);
    }

    public SDAnimSet with(SDAnimSet with)
    {
        initAnim(with);
        SDAnim save = getSaveAnim();
        getSet().play(save.get()).with(with.get());
        saveAnim(with);
        return with;
    }

    public SDAnimSet next()
    {
        return next(new SDAnimSet());
    }

    public SDAnimSet next(View target)
    {
        SDAnimSet anim = new SDAnimSet();
        anim.setTarget(target);
        return next(anim);
    }

    public SDAnimSet next(SDAnimSet next)
    {
        initAnim(next);
        SDAnim save = getSaveAnim();
        getSet().play(next.get()).after(save.get());
        saveAnim(next);
        return next;
    }

    public SDAnimSet delay(long time)
    {
        SDAnimSet delay = null;
        if (isEmptyProperty())
        {
            delay = this;
        } else
        {
            delay = next();
        }
        delay.setAlpha(delay.getTargetView().getAlpha()).setDuration(time);
        return delay;
    }

    public AnimatorSet getSet()
    {
        if (parentAnim != null)
        {
            return parentAnim.getSet();
        } else
        {
            return animatorSet;
        }
    }

    public void start()
    {
        getSet().start();
    }

    // overide

    public static SDAnimSet from(View target)
    {
        SDAnimSet parent = new SDAnimSet();
        parent.setTarget(target);
        return parent.reset();
    }

    @Override
    public SDAnimSet setTarget(View target)
    {
        super.setTarget(target);
        return this;
    }

    @Override
    public SDAnimSet setDuration(long duration)
    {
        super.setDuration(duration);
        return this;
    }

    @Override
    public SDAnimSet setAccelerate()
    {
        super.setAccelerate();
        return this;
    }

    @Override
    public SDAnimSet setAccelerateDecelerate()
    {
        super.setAccelerateDecelerate();
        return this;
    }

    @Override
    public SDAnimSet setAlpha(float... values)
    {
        super.setAlpha(values);
        return this;
    }

    @Override
    public SDAnimSet setDecelerate()
    {
        super.setDecelerate();
        return this;
    }

    @Override
    public SDAnimSet setInterpolator(Interpolator interpolator)
    {
        super.setInterpolator(interpolator);
        return this;
    }

    @Override
    public SDAnimSet setLinear()
    {
        super.setLinear();
        return this;
    }

    @Override
    public SDAnimSet setRepeatCount(int count)
    {
        super.setRepeatCount(count);
        return this;
    }

    @Override
    public SDAnimSet setRotation()
    {
        super.setRotation();
        return this;
    }

    @Override
    public SDAnimSet setRotation(float... values)
    {
        super.setRotation(values);
        return this;
    }

    @Override
    public SDAnimSet setRotationReverse()
    {
        super.setRotationReverse();
        return this;
    }

    @Override
    public SDAnimSet setX(float... values)
    {
        super.setX(values);
        return this;
    }

    @Override
    public SDAnimSet setY(float... values)
    {
        super.setY(values);
        return this;
    }

    @Override
    public SDAnimSet setScaleX(float... values)
    {
        super.setScaleX(values);
        return this;
    }

    @Override
    public SDAnimSet setScaleY(float... values)
    {
        super.setScaleY(values);
        return this;
    }

    @Override
    public SDAnimSet addListener(Animator.AnimatorListener listener)
    {
        super.addListener(listener);
        return this;
    }

    @Override
    public SDAnimSet setStartDelay(long delay)
    {
        super.setStartDelay(delay);
        return this;
    }
}
