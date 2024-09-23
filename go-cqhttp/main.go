// Package main
package main

import (
	"context"
	"net"
	"os"
	"path/filepath"

	"github.com/Mrs4s/go-cqhttp/cmd/gocq"
	"github.com/Mrs4s/go-cqhttp/global/terminal"

	_ "unsafe"

	_ "github.com/Mrs4s/go-cqhttp/db/leveldb"   // leveldb 数据库支持
	_ "github.com/Mrs4s/go-cqhttp/modules/silk" // silk编码模块
	// 其他模块
	// _ "github.com/Mrs4s/go-cqhttp/db/sqlite3"   // sqlite3 数据库支持
	// _ "github.com/Mrs4s/go-cqhttp/db/mongodb"    // mongodb 数据库支持
	// _ "github.com/Mrs4s/go-cqhttp/modules/pprof" // pprof 性能分析
)

func main() {
	// 为子进程时会整个修改上级进程的terminal title，所以关闭
	// terminal.SetTitle()
	dnsHack()
	addFFmpegToPath()
	gocq.InitBase()
	gocq.PrepareData()
	gocq.LoginInteract()
	_ = terminal.DisableQuickEdit()
	_ = terminal.EnableVT100()
	gocq.WaitSignal()
	_ = terminal.RestoreInputMode()
}

func dnsHack() {
	var (
		dnsResolverIP    = "114.114.114.114:53" // Google DNS resolver.
		dnsResolverProto = "udp"                // Protocol to use for the DNS resolver
	)
	var dialer net.Dialer
	net.DefaultResolver = &net.Resolver{
		PreferGo: false,
		Dial: func(context context.Context, _, _ string) (net.Conn, error) {
			conn, err := dialer.DialContext(context, dnsResolverProto, dnsResolverIP)
			if err != nil {
				return nil, err
			}
			return conn, nil
		},
	}
}

func addFFmpegToPath() {
	dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
	if err != nil {
		return
	}
	ffmpegPaths := []string{
		filepath.Join(dir, "ffmpeg"),
		filepath.Join(dir, "ffmpeg/bin"),
		filepath.Join(dir, "../ffmpeg"),
		filepath.Join(dir, "../ffmpeg/bin"),
		"./ffmpeg",
		"./ffmpeg/bin",
		"../ffmpeg",
		"../ffmpeg/bin",
	}

	uniquePaths := make(map[string]bool)
	for _, path := range ffmpegPaths {
		absPath, _ := filepath.Abs(path)
		uniquePaths[absPath] = true
	}

	for path := range uniquePaths {
		_ = os.Setenv("PATH", path+string(os.PathListSeparator)+os.Getenv("PATH"))
	}
}
