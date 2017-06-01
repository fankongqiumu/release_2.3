package com.fanwe.library.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.fanwe.library.SDLibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

public class SDOtherUtil
{

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void copyText(CharSequence content)
    {
        if (getBuildVersion() < 11)
        {
            android.text.ClipboardManager clip = getOldClipboardManager();
            clip.setText(content);
        } else
        {
            android.content.ClipboardManager clip = getNewClipboardManager();
            clip.setPrimaryClip(ClipData.newPlainText(null, content));
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static CharSequence pasteText()
    {
        CharSequence content = null;
        if (getBuildVersion() < 11)
        {
            android.text.ClipboardManager clip = getOldClipboardManager();
            if (clip.hasText())
            {
                content = clip.getText();
            }
        } else
        {
            android.content.ClipboardManager clip = getNewClipboardManager();
            if (clip.hasPrimaryClip())
            {
                content = clip.getPrimaryClip().getItemAt(0).getText();
            }
        }
        return content;
    }

    @SuppressWarnings("deprecation")
    public static android.text.ClipboardManager getOldClipboardManager()
    {
        android.text.ClipboardManager clip = (android.text.ClipboardManager) SDLibrary.getInstance().getApplication()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return clip;
    }

    public static android.content.ClipboardManager getNewClipboardManager()
    {
        android.content.ClipboardManager clip = (android.content.ClipboardManager) SDLibrary.getInstance().getApplication()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return clip;
    }

    public static int getBuildVersion()
    {
        return Build.VERSION.SDK_INT;
    }

    public static String hideMobile(String mobile)
    {
        String result = null;
        if (!TextUtils.isEmpty(mobile) && TextUtils.isDigitsOnly(mobile) && mobile.length() == 11)
        {
            result = mobile.substring(0, 3) + "****" + mobile.substring(7);
        }
        return result;
    }

    public static Type[] getType(Class<?> clazz)
    {
        Type[] types = null;
        if (clazz != null)
        {
            Type type = clazz.getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            types = parameterizedType.getActualTypeArguments();
        }
        return types;
    }

    public static Type getType(Class<?> clazz, int index)
    {
        Type type = null;
        Type[] types = getType(clazz);
        if (types != null && index >= 0 && types.length > index)
        {
            type = types[index];
        }
        return type;
    }

    public static String build(Object... content)
    {
        return build(",", content);
    }

    public static String build(String separator, Object... content)
    {
        if (separator == null)
        {
            separator = "";
        }
        StringBuilder sb = new StringBuilder();
        if (content != null && content.length > 0)
        {
            for (Object obj : content)
            {
                if (obj != null)
                {
                    sb.append(obj.toString()).append(separator);
                }
            }
        }
        return sb.toString();
    }

    public static String buildDefaultString(String defaultText, String text, String before, String after)
    {
        String result = null;
        if (text.startsWith(before))
        {
            if (text.contains(after))
            {
                String removedBefore = text.replaceFirst(before, "");
                String start = removedBefore.substring(0, removedBefore.indexOf(after));
                String end = removedBefore.substring(removedBefore.indexOf(after) + after.length());

                result = start + defaultText + end;
            } else
            {
                result = text.replaceFirst(before, "") + defaultText;
            }
        } else if (text.startsWith(after))
        {
            result = defaultText + text.replaceFirst(after, "");
        } else
        {
            result = defaultText + text;
        }
        return result;
    }

    /**
     * 把文件加到相册
     *
     * @param file
     */
    public static void scanFile(Context context, File file)
    {
        if (context != null && file != null && file.exists())
        {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
        }
    }

    public static boolean isMiui()
    {
        String miuiName = getMiuiName();
        return !TextUtils.isEmpty(miuiName);
    }

    public static boolean isMiuiV8()
    {
        String miuiName = getMiuiName();
        return "V8".equals(miuiName);
    }

    public static String getMiuiName()
    {
        Properties prop = new Properties();
        try
        {
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        String miuiName = prop.getProperty("ro.miui.ui.version.name", null);
        return miuiName;
    }

    /**
     * 耳机是否已经插入
     *
     * @param context
     * @return
     */
    public static boolean isHeadsetPlug(Context context)
    {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.isWiredHeadsetOn();
    }

    public static long getTotalCpuTime()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();

            String[] cpuInfos = load.split(" ");

            long cpu = Long.parseLong(cpuInfos[2])
                    + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                    + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                    + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);

            return cpu;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getAppCpuTime()
    {
        try
        {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();

            String[] cpuInfos = load.split(" ");
            long cpu = Long.parseLong(cpuInfos[13])
                    + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                    + Long.parseLong(cpuInfos[16]);

            return cpu;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static int[] getCpuRate()
    {
        int[] arrCpu = new int[2];
        try
        {
            String strLine;
            Process p = Runtime.getRuntime().exec("top -n 1");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((strLine = br.readLine()) != null)
            {
                if (strLine.trim().length() < 1)
                {
                    continue;
                } else
                {
                    String[] arrInfo = strLine.split("%");

                    String appCpu = arrInfo[0].replace("User ", "");
                    String systemCpu = arrInfo[1].replace(", System ", "");

                    arrCpu[0] = Integer.valueOf(appCpu);
                    arrCpu[1] = Integer.valueOf(systemCpu);
                    break;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return arrCpu;
    }

    /**
     * 域名是否可用，只能在后台线程调用
     *
     * @param host
     * @return
     */
    public static boolean isHostAvailable(String host)
    {
        try
        {
            InetAddress inetAddress = InetAddress.getByName(host);
            String ip = inetAddress.getHostAddress();
            if (!TextUtils.isEmpty(ip))
            {
                return true;
            } else
            {
                return false;
            }
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static String getHost(String url)
    {
        try
        {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
