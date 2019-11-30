addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")
addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.12.2")
credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += "com-mvn" at "https://repo.lightbend.com/commercial-releases/"
resolvers += Resolver.url("com-ivy", url("https://repo.lightbend.com/commercial-releases/"))(Resolver.ivyStylePatterns)
resolvers += Resolver.url("lightbend-commercial", url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)