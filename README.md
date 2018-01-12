# TraceGPPontoDump

Utilitário CLI para exportar os registros de ponto do TraceGP.

É exportado um arquivo *Ponto.csv* com os registros e o *Ponto.png* com o Screenshot da consulta de onde os dados são obtidos.

Este utilitário utiliza a técnica de Web Crawling com Selenium e não uma API proprietária do TraceGP. **Qualquer mudança na estrutura do TraceGP pode quebrar este aplicativo.**

## Uso

java -jar TraceGPPontoDump.jar

​    -e,--endereco \<arg\> Endereço do TraceGP

​    -u,--usuario \<arg\> Usuário do TraceGP

​    -s,--senha \<arg\> Senha do TraceGP

​    -di,--dataInicial \<arg\> Data inicial

​    -df,--dataFinal \<arg\> Data final
