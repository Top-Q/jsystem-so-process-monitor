import sys
import wmi
from optparse import OptionParser
from win32api import GetFileVersionInfo, LOWORD, HIWORD

if __name__ == '__main__':
    usageText = "cpu.py [options]\n" + \
        "example: python cpu.py -p notepad.exe"
    
    parser = OptionParser(usage = usageText)
    
    parser.add_option( "-p",
                        dest = "processName",
                        help = "Process name",
                        type = "string" )

    parser.add_option( "-i",
                        dest = "dllName",
                        help = "DLL full path",
                        type = "string" )

    parser.add_option( "-e",
                        dest = "processExist",
                        help = "Process exists",
                        type = "string" )

    parser.add_option( "-s",
                        dest = "serviceStatus",
                        help = "Service status",
                        type = "string" )

    (options, args) = parser.parse_args(sys.argv)

	# Create WMI object
	# Memory (Private Working Set) = p.WorkingSetPrivate
    c = wmi.WMI()
    if options.processName:
        # Get the process
        process = c.Win32_Process(name=options.processName)[0]
        # Get the CPU & Memory
        p = c.Win32_PerfFormattedData_PerfProc_Process(IdProcess=process.ProcessID)[0]
        print("CPU:\t%s" % (p.PercentProcessorTime))        
        print("Memory:\t%s" % (int(process.WorkingSetSize) / 1024))
        print("Virtual Memory:\t%s" % (int(process.VirtualSize) / 1024))
        print("Private Bytes:\t%s" % (int(p.PrivateBytes) / 1024)) 
        print("Thread Count:\t%s" % (process.ThreadCount)) 
        print("Handle Count:\t%s" % (process.HandleCount))
    elif options.processExist:
        if len(c.Win32_Process(name=options.processExist)) == 0:
            print("Process %s doesn't exist\n" % options.processExist)
        else:
            print("Process %s exists\n" % options.processExist)
    elif options.serviceStatus:
		if len(c.Win32_Service(Caption=options.serviceStatus, State="Running")) > 0:
			print("Service %s running\n" % options.serviceStatus)
		else:
			print("Service %s stopped\n" % options.serviceStatus)    	
    elif options.dllName:
        info = GetFileVersionInfo (options.dllName, "\\")
        ms = info['FileVersionMS']
        ls = info['FileVersionLS']
        print "Version:\t%s.%s.%s.%s" % (HIWORD(ms), LOWORD(ms), HIWORD(ls), LOWORD(ls))
    else:
        print usageText



