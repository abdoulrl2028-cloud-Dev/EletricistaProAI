# Eletricista Pro AI

Aplicativo Android da versão 0.1. O jogo Unity em `2Dgaming` não foi alterado.

Abra esta pasta no Android Studio. O SDK esperado está em `local.properties`, que fica fora do Git.

## O que esta versão faz

- Calcula a Lei de Ohm, a potência em corrente contínua, a corrente estimada e o consumo.
- Apresenta uma aula de Lei de Ohm e corrige o exercício no aparelho.
- Lê os preços de teste em `app/src/main/assets/plans.json`.
- Traduz a interface para português, francês e inglês. O texto da aula ainda está em português.
- Recusa HTTP sem cifra.

## O que ainda não existe

Projetos, planta 2D, visualização 3D, simulador elétrico, assistente de IA, catálogo, orçamento em PDF, login e pagamento. As telas correspondentes dizem isso. Nenhuma compra é iniciada.

O registro de aula concluída é progresso local. Não é qualificação profissional reconhecida.

## Instalar no telefone

A versão publicada é um APK de teste, fora da Play Store. No Android, baixe o arquivo da release `v0.1.0`, permita a instalação por arquivo e abra o APK. A chave usada para assinar fica apenas neste computador, na pasta `signing`, e não entra no Git.

## Executar os testes de cálculo

```bash
export JAVA_HOME=/home/delll/.local/jdks/jdk-21.0.12.1+1
./gradlew :calculators:test :app:assembleDebug
```
