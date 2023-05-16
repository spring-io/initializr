plugins {
	id('com.example') version '1.0.0.RELEASE'
	id('java')
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
	mavenCentral()
}

ext {
	set('acmeVersion', "Brussels.SR2")
}

dependencies {
	implementation 'com.example.acme:library'
	testImplementation 'com.example.acme:library-test'
}

dependencyManagement {
	imports {
		mavenBom "com.example.acme:library-bom:${acmeVersion}"
	}
}

