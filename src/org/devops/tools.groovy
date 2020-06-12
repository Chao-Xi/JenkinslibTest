package org.devops

//格式化输出
def PrintMes(value,color){
    colors = ['red'   : "\033[31m >>>>>>>>>>>${value}<<<<<<<<<<< \033[0m",
              'blue'  : "\033[34m >>>>>>>>>>>${value}>>>>>>>>>>> \033[0m",
              'green' : "\033[32m>>>>>>>>>>${value}>>>>>>>>>>\033[0m",
              'Magneta' : "\033[35m >>>>>>>>>>>${value}<<<<<<<<<<< \033[0m" ]
    ansiColor('xterm') {
        println(colors[color])
    }
}
