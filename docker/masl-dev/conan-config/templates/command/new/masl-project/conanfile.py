import conan

class ConanFile(conan.ConanFile):
    name = "{{name}}-masl"
    version = "{{version if version is defined else '0.1'}}"
    user = "{{user if user is defined else 'xtuml'}}"

    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    exports_sources= "src/*"

    def requirements(self):
        {% if requires is defined -%}
            {% for require in requires -%}
        self.requires("{{ require }}")
            {% endfor %}
        {%- endif %}
        {% if tool_requires is defined -%}
            {% for require in tool_requires -%}
        self.tool_requires("{{ require }}")
            {% endfor %}
        {%- endif %}
        super().requirements(self)
