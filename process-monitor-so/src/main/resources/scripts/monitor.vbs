strComputer = "."

Set objWMIService = GetObject("winmgmts:\\" & strComputer & "\root\cimv2")
Set PerfProcess = objWMIService.Get("Win32_PerfFormattedData_PerfProc_Process.Name='MCC'")

While(True)
    PerfProcess.Refresh_	
	If PerfProcess.PercentProcessorTime <> "" Then
		WScript.StdOut.Write "CPU:" & PerfProcess.PercentProcessorTime & ";"
		WScript.StdOut.Write "Private Bytes:" & (PerfProcess.PrivateBytes / 1024) & ";"
		Set colProcesses = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'MCC.exe'")
		For Each objProcess in colProcesses
			WScript.StdOut.Write "Memory:" & (objProcess.WorkingSetSize / 1024) & ";"
			WScript.StdOut.Write "Virtual Memory:" & (objProcess.VirtualSize / 1024) & ";"
			WScript.StdOut.Write "Thread Count:" & objProcess.ThreadCount & ";"
			WScript.StdOut.Write "Handle Count:" & objProcess.HandleCount
			WScript.StdOut.WriteLine 
		Next
	End If
    Wscript.Sleep 250
Wend




