#!/usr/bin/env python
# coding:utf-8
# author:yangfan

import sys


def reduce(html_file, output_file):
    in_data_range = False
    data_section = 0
    pid = ''
    package_name = 'com.xxx.xxx' #App的包名

    with open(html_file, 'r', encoding="utf-8") as file_in, open(output_file, 'wb') as file_out:
        for line in file_in.readlines():
            if in_data_range:
                if 'class="trace-data"' in line:
                    # 标记是第几个 trace-data 区
                    data_section += 1

                elif '</script>' in line:
                    pass

                else:
                    if data_section == 1:
                        # 在第一个 trace-data 区寻找 pid
                        rs_line = line.rstrip()
                        if rs_line.endswith(package_name) or rs_line.endswith(package_name):
                            pid = line.split()[1]

                    elif data_section == 2:
                        # 处理第二个 trace-data 区
                        if 'tracing_mark_write' not in line:
                            # 去除 tracing_mark 以外的事件
                            continue

                        elif 'trace_event_clock_sync' in line:
                            # 略过 clock_sync 事件
                            pass

                        elif not line.split()[0].endswith('-' + pid):
                            # 去除 UI 线程以外的事件
                            continue

            elif line.startswith('<!-- BEGIN TRACE -->'):
                in_data_range = True

            file_out.write(bytes(line, encoding='UTF-8'))


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print('Usage: %s input_html output_html' % sys.argv[0])
        sys.exit(-1)
    reduce(sys.argv[1], sys.argv[2])

