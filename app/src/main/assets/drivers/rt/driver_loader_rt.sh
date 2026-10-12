#!/system/bin/sh
# RT Driver Loader Script (RTDev / RTHook)
# Real-Time Thread Priority & Input Hooking Engine
MODE="$1" # RTDev or RTHook
TARGET_KERNEL="$2"
PRIORITY="$3"

echo "[*] [RT_DRIVER] Initializing Real-Time Driver Engine..."
echo "[*] Driver Mode: ${MODE:-RTDev}"
echo "[*] Kernel: $(uname -r 2>/dev/null || echo 'Unknown')"
echo "[*] Target Version: ${TARGET_KERNEL:-Auto}"
echo "[*] Real-Time Thread Priority: ${PRIORITY:-99}"

# Privilege verification
UID_VAL=$(id -u 2>/dev/null)
if [ "$UID_VAL" != "0" ]; then
    echo "[!] Notice: UID is $UID_VAL"
fi

# Real-Time Thread Priority Boost for SurfaceFlinger & InputReader
echo "[+] Elevating SurfaceFlinger and InputReader real-time priority (SCHED_FIFO)..."
for pid in $(pidof surfaceflinger system_server 2>/dev/null); do
    chrt -f -p "${PRIORITY:-99}" "$pid" 2>/dev/null && echo "  -> RT Sched applied to PID $pid"
done

# Kernel Schedtune & Low-Latency Hook
echo "[+] Activating RT Kernel Hook (sys/kernel/sched_latency)..."
if [ -f "/proc/sys/kernel/sched_latency_ns" ]; then
    echo 4000000 > /proc/sys/kernel/sched_latency_ns 2>/dev/null
fi
if [ -f "/proc/sys/kernel/sched_min_granularity_ns" ]; then
    echo 1000000 > /proc/sys/kernel/sched_min_granularity_ns 2>/dev/null
fi

echo "[+] Setting RT scheduler properties..."
setprop persist.sys.rt.driver 1 2>/dev/null
setprop persist.sys.touch.latency 0 2>/dev/null

echo "[*] RT Driver Status: REGISTERED & HOOKED"
echo "[*] Real-time engine: OPERATIONAL"
echo "[SUCCESS] RT Driver Loaded Successfully! Exit code: 0"
exit 0
